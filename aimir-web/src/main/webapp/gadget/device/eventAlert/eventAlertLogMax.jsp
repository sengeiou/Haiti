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
        .x-menu-list-item span, .x-menu-list-item a {
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
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
     <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/stomp.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
	    
        /* severity color 설정 변수*/
        var red = '#F31523'; //critical
		var orange = '#E76F09'; //major
		var yellow = '#E0A504'; //minor
		var green = '#0E9C0C'; //warning
		var blue = '#12ABBA'; //information
		var purple = '#5E32BB'; //normal
        
		var supplierId = "${supplierId}";

        var fcChartLogTypeDataXml;
        var fcChartLogType;

        var fcChartLogMessageDataXml;
        var fcChartLogMessage;
        var messageId = '';
        // 수정권한
        var editAuth = "${editAuth}";

        /**
         * 유저 세션 정보 가져오기
         */
        /* $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                    }
                }
        ); */

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

        function getHeaderMessage() {
            var headerMessage = new Array();
            var idx = 0;

            headerMessage[idx++] = "<fmt:message key='aimir.number'/>";
            headerMessage[idx++] = "<fmt:message key='aimir.header.severitylevel'/>";      // Severity Level
            headerMessage[idx++] = "<fmt:message key='aimir.header.type'/>";               // Type
            headerMessage[idx++] = "<fmt:message key='aimir.header.msg'/>";                // Message
            headerMessage[idx++] = "<fmt:message key='aimir.location'/>";
            headerMessage[idx++] = "<fmt:message key='aimir.activatorId'/>";
            headerMessage[idx++] = "<fmt:message key='aimir.activatorType'/>";
            headerMessage[idx++] = "<fmt:message key='aimir.status'/>";
            headerMessage[idx++] = "<fmt:message key='aimir.opentime'/>";
            headerMessage[idx++] = "<fmt:message key='aimir.closetime'/>";
            headerMessage[idx++] = "<fmt:message key='aimir.duration'/>";
            headerMessage[idx++] = "<fmt:message key='aimir.header.eventclassname'/>";     // Event Class Name/Event Alert Name
            headerMessage[idx++] = "<fmt:message key='aimir.writetime'/>";

            return headerMessage;
        }

        var values = new Array();
        var gridWidth = 0;
        //#######Event alarm history Grid List func start

        //EventAlarmLogGrid propeties
        var EventAlarmLogGridInstanceOn = false;
        var EventAlarmLogGrid;
        var EventAlarmLogColModel;
        var EventAlarmLogCheckSelModel;
        var EventAlarmLogGridStore;

        //Event alarm history Grid List fetch js func.
        function getEventAlarmLogGrid() {
            var width = $("#EventAlarmLogGridDiv").width();
            gridWidth = width;
            var rowSize = 10;

            //검색 조건을 가지고온다.
            values = getCondition();

            //### EventAlarmLogGrid Store fetch
            EventAlarmLogGridStore = new Ext.data.JsonStore({
                //autoLoad : {params:{start: 0, limit: rowSize, page: page}},
                autoLoad : {params:{start: 0, limit: rowSize}},
                url : "${ctx}/gadget/device/eventAlert/getEventAlramLogHistory.do",
                //파라매터 설정.
                baseParams : {
                    pageSize : rowSize,
                    supplierId : supplierId,
                    values : values
                },
                //Total Cnt
                totalProperty : 'eventalertloghistorytotal',
                root : 'eventAlertLogList',
                fields : [
                          "idx"
                         ,"severity"
                         ,"eventAlertId"
                         ,"eventAlertName"
                         ,"type"
                         ,"message"
                         ,"location"
                         ,"activatorId"
                         ,"activatorType"
                         ,"activatorIp"
                         ,"status"
                         ,"writeTime"
                         ,"openTime"
                         ,"closeTime"
                         ,"duration"
                         ,"eventLogId"
                         ],
                listeners : {
                    beforeload : function(store, options) {
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    }
                }
            });//comlogStore End

            var headers = getHeaderMessage();
            var colWidth = (width-50)/11-chromeColAdd;
            // EventAlarmLogGrid Model DEfine
            EventAlarmLogGridModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: headers[0],  dataIndex: 'idx',            tooltip: headers[0],  width: 50}
                   ,{header: headers[1],  dataIndex: 'severity',       tooltip: headers[1], width: colWidth-50, renderer : severityCol}
                   ,{header: headers[2],  dataIndex: 'type',           tooltip: headers[2],  width: colWidth-80}
                   ,{header: headers[11], dataIndex: 'eventAlertName', tooltip: headers[11], width: colWidth+10, align: "left", editor:new Ext.form.TextField({allowBlank:false})}
                   ,{header: headers[3],  dataIndex: 'message',        tooltip: headers[3],  width: colWidth+70, align: "left", editor:new Ext.form.TextField({allowBlank:false})}
                   ,{header: headers[4],  dataIndex: 'location',       tooltip: headers[4],  width: colWidth-55, align: "left", editor:new Ext.form.TextField({allowBlank:false})}
                   ,{header: headers[5],  dataIndex: "activatorId",    tooltip: headers[5],  width: colWidth+10, editor:new Ext.form.TextField({allowBlank:false})}
                   ,{header: headers[6],  dataIndex: 'activatorType',  tooltip: headers[6],  width: colWidth-20}
                   ,{header: headers[7],  dataIndex: 'status',         tooltip: headers[7],  width: colWidth-35}
                   ,{header: headers[12], dataIndex: 'writeTime',      tooltip: headers[12], editor:new Ext.form.TextField({allowBlank:false})}
                   ,{header: headers[8],  dataIndex: 'openTime',       tooltip: headers[8], editor:new Ext.form.TextField({allowBlank:false})}
                   ,{header: headers[9],  dataIndex: 'closeTime',      tooltip: headers[9]}
                   ,{header: headers[10], dataIndex: 'duration',       tooltip: headers[10]}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: colWidth
                   ,align: "center"
                   ,renderer: addTooltip
                }
            });
            
    		
    		var setColorFrontTag = "<b style=\"color:";
			var setColorMiddleTag = ";\">"; 
    		var setColorBackTag = "</b>";
    		
            function severityCol(val){
            	switch (val) {
				case "Critical" :
					return setColorFrontTag + red + setColorMiddleTag + val + setColorBackTag ;
					break;
				case "Major" :
					return setColorFrontTag + orange + setColorMiddleTag + val + setColorBackTag ;
					break;
				case "Minor" :
					return setColorFrontTag + yellow + setColorMiddleTag + val + setColorBackTag ;
					break;
				case "Warning" : 
					return setColorFrontTag + green + setColorMiddleTag + val + setColorBackTag ;
					break;
				case "Information" :
					return setColorFrontTag + blue + setColorMiddleTag + val + setColorBackTag ;
					break;
				case "Normal" :
					return setColorFrontTag + purple + setColorMiddleTag + val + setColorBackTag ;
					break;
				default:
					return val;
					break;
				}
    		}

            if (EventAlarmLogGridInstanceOn == false) {
                EventAlarmLogGridPanel = new Ext.grid.EditorGridPanel({
                	clicksToEdit: 1,
                	store : EventAlarmLogGridStore,
                    colModel : EventAlarmLogGridModel,
                    sm : new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll : false,
                    scroll : false,
                    width : width,
                    //패널 높이 설정
                    height : 288,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg: 'loading...'
                    },
                    renderTo : 'EventAlarmLogGridDiv',
                    viewConfig : {
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar : new Ext.PagingToolbar({
                        pageSize : rowSize,
                        store : EventAlarmLogGridStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
                    }),
                    listeners: {
                        rowcontextmenu: function(grid, index, event) {
                            if (editAuth == "true") {
                                grid.selectedNode = grid.store.getAt(index);  // we need this
                                if (index != null) {
                                    this.getSelectionModel().selectRow(index);
                                }

                                showHistoryGridMenu(grid, index, event);
                            }
                        }
                    }
                });
                EventAlarmLogGridInstanceOn = true;
            } else {
                EventAlarmLogGridPanel.setWidth(width);
                var bottomToolbar = EventAlarmLogGridPanel.getBottomToolbar();
                EventAlarmLogGridPanel.reconfigure(EventAlarmLogGridStore, EventAlarmLogGridModel);
                bottomToolbar.bindStore(EventAlarmLogGridStore);
            }

            hide();
        };//func EventAlarmLogGridList End

        // Event Alert Log History Grid Context Menu
        function showHistoryGridMenu(grid, index, event) {
            event.stopEvent();
            var record = grid.getStore().getAt(index);

            var menu = new Ext.menu.Menu({
                showSeparator : false,
                cls: "no-icon-menu",
                items : [
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
                                                   if (json != null && json.result.toLowerCase() == "success") {
                                                       Ext.Msg.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>",
                                                               function() {
                                                                   EventAlarmLogGridStore.reload();
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

        //########################################################################
        //########### real time log grid Js.Start
        //########################################################################

        // 인터벌 설정
        //var interval = ${interval};
        var interval = 60000;
        // grid 에서 삭제할 데이터 인터벌
        var gridRemoveInterval = interval + 10000;
        // queue 에서 삭제 인터벌
        var removeInterval = 60000;

        $(document).ready(function() {
            getRealTime();
            updateFChart();

            getEventAlertLogRealTimeMax();

            //console.log("interval:" + interval);
            //인터벌 함수 설정.
            //setInterval("insertDataToStore()", interval);
            //setInterval("deleteQueueData()", removeInterval);
            //setInterval(function(){insertDataToStore();}, interval);
            //setTimeout(function(){insertDataToStore();}, interval);
            
            //최초 실행 한번후에 주기적 동작
            insertStompDataToStore();
            runInsertStompDataToStore();
            keepalive();
            
            //setInterval(function(){deleteQueueData();}, interval);
            //initMessage();
            hide();
        });

        //윈도우 리싸이즈시 event
        $(window).resize(function() {
            if (!$("#EventAlertLogRealTimeMaxDiv").is(":hidden")) {
                getEventAlertLogRealTimeMax();
            }

            if (!$("#EventAlarmLogGridDiv").is(":hidden")) {
                getEventAlarmLogGrid();
            }
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
        
        //stomp topic
        function runInsertStompDataToStore() {
        	setTimeout(function(){insertStompDataToStore()},interval);
        	//console.log(interval);
        	//insertStompDataToStore();
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
        		if(JSONdata.writeTime.length==14){
        			yyyy = JSONdata.writeTime.substr(0,4);
            		mm = JSONdata.writeTime.substr(4,2);
            		dd = JSONdata.writeTime.substr(6,2);
            		hh = JSONdata.writeTime.substr(8,2);
            		nn = JSONdata.writeTime.substr(10,2);
            		ss = JSONdata.writeTime.substr(12,2);        		
            		dcTime = new Date(yyyy,mm,dd,hh,nn,ss);
            		dcTime.setMonth(dcTime.getMonth()-1);
            		JSONdata.writeTime = dcTime.toLocaleString(lang);
        		}
        		var	jsonRecord = new Ext.data.Record(JSONdata);
         			        		        		            		
        		// 패널에서 스토어 추출하여insert
        		EventAlertLogRealTimeMaxPanel.getStore().insert(0,jsonRecord);
        		
        		// 시간 검증
        		last_time = new Date().getTime(); 
        		last_msg = message.body;
        	}
        }
        
        //stomp topic
        var connect_callback = function() {
        	for(j=0; j<client.length; j++){
        		if(client[j].ws.readyState == 1)
        			client[j].subscribe("/topic/AiMiR.Event", message_callback, {id:"client-stomp-"+j});
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
        			//console.log('insertStompDataToStore MAX');
        			if (data.lang >0){
        				lang = data.lang;
        				country = data.country;
        			}else{
        				lang = window.navigator.userLanguage || window.navigator.language;        				
        			}
        			
        			if (data.wslen > 0){
        				//var tt = Ext.global.document.domain;
        				//wsUrl = data.wsurl;
        				//wsUrl = ["ws://187.1.30.70:61614", "ws://localhost:61614"];
        				//data.wslen =2;        				
        				for(i=0; i<data.wslen; i++){
        					var propUrl = data.wsurl[i];
        					if(propUrl.includes('localhost')){
        						wsUrl[i] = "ws://"+Ext.global.document.domain+":61614/"; 
        					}else
        						wsUrl[i] = propUrl;
        				}
        			}else{
        				//wsUrl = "ws://localhost:61614/";
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
          //  setTimeout(function(){insertDataToStore();}, interval);
        }

        var isWork = false;
        var checkCount = 0;
        var startTime = "";
        //////////////////////////////////////
        var totalQueueLength = 0;
        var totalListLength = 0;
        //////////////////////////////////////
        function insertDataToStore() {
            //console.log("insertDataToStore");
            $.ajax({
                type : "POST",
                data : {
                    supplierId : supplierId,
                    interval : interval,
                    startTime : startTime
                },
                dataType : "json",
                //getRealTimeEventAlertLogFromActiveMq
                url : "${ctx}/gadget/device/eventAlert/getRealTimeEventAlertLogFromActiveMq.do",
                success : function(data, status) {
                    //console.log("data:", data);
                    //console.log("status:", status);
                    if (data.startTime != null && data.startTime != "") {
                        startTime = data.startTime;
                    }
                    ///////////////
                    //var qlen = data.queueLength;
                    //var listlen = data.logList.length;
                    //totalQueueLength = totalQueueLength + data.queueLength;
                    //totalListLength = totalListLength + data.logList.length;
                    //console.log("queueLength:" + qlen + ", listSize:" + listlen + ", totalQueueLen:" + totalQueueLength + ", totalLisLen:" + totalListLength);
                    ///////////////
                    // 메시지 queue 에 데이타가 존재하면. grid store에 queue log adding
                    if (data.queueLength > 0) {
                        var arrayList = data.logList;
                        var recordFrame = Ext.data.Record.create([
                            {name: 'eventMessage'},
                            {name: 'location'},
                            {name: 'activatorId'},
                            {name: 'activatorType'},
                            {name: 'activatorIp'},
                            {name: 'status'},
                            {name: 'writeTime'},
                            {name: 'openTime'},
                            {name: 'closeTime'},
                            {name: 'closeTimenf'},
                            {name: 'duration'},
                            {name: 'eventLogId'}
                        ]);

                        var len = 0;
                        var dataMap = null;
                        var delRecord = null;
                        var eventLogId;
                        $.each(arrayList, function(i) {
                            dataMap = arrayList[i];
                            delRecord = null;
                            eventLogId = dataMap.eventLogId;

                            //추가 데이터 정의
                            var record = new recordFrame({
                                    eventMessage : dataMap.eventMessage ,
                                    location : dataMap.location,
                                    activatorId : dataMap.activatorId,
                                    activatorType : dataMap.activatorType,
                                    activatorIp : dataMap.activatorIp,
                                    status : dataMap.status,
                                    writeTime : dataMap.writeTime,
                                    openTime : dataMap.openTime,
                                    closeTime : dataMap.closeTime,
                                    closeTimenf : dataMap.closeTimenf,
                                    duration : dataMap.duration,
                                    eventLogId : eventLogId
                            });

                            len = EventAlertLogRealTimeMaxStore.getCount();

                            if (len > 0) {
                                for (var i = 0; i < len; i++) {
                                    delRecord = EventAlertLogRealTimeMaxStore.getAt(i);
                                    if (eventLogId == delRecord.data.eventLogId) {
                                        EventAlertLogRealTimeMaxStore.remove(delRecord);
                                        //len--;
                                        break;
                                    }
                                }
                            }

                            //store에 실시간 데이타 insert
                            EventAlertLogRealTimeMaxStore.insert(0, record);
                            //len++;
                        });
                        getEventAlertLogRealTimeMax();
                        //EventAlertLogRealTimeMaxStore.reload();
                    }

                    // Cleared/Cleared Manually 데이터 삭제:주석처리
                    //removeClearedData();
                    //deleteQueueData();
                    //isWork = false;
                    runInsertDataToStore();
                },
                error : function(request, status) {
                    isWork = false;
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
                    runInsertDataToStore();
                }
            });
        }

        /* function deleteQueueData() {
            //console.log("deleteQueueData max");
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
                        console.log("result max:", data.result);
                    }
                },
                error : function(request, status) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',status);
                }
            });
        } */

        /* function getGridCount() {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',EventAlertLogRealTimeMaxStore.getCount());
        } */

        /* var dummy = 1;
        var indx = 0;
        var clientId = (new Date()).getTime().toString();
        function pollMessage() {
            $.ajax({
                type : 'GET',
                data : {
                    dummy : dummy++,
                    clientId : clientId
                },
                url : '${ctx}/broadcaster',
                success: function (msg) {
                    if (msg == null || msg == "") return;
                    //console.log("["+(indx++)+"] msg:", ">" + msg + "<");
                    
                    if (data.startTime != null && data.startTime != "") {
                        startTime = data.startTime;
                    }
                    // 메시지 queue 에 데이타가 존재하면. grid store에 queue log adding
                    if (data.queueLength > 0) {
                        var arrayList = data.logList;
                        var recordFrame = Ext.data.Record.create([
                            {name: 'eventMessage'},
                            {name: 'location'},
                            {name: 'activatorId'},
                            {name: 'activatorType'},
                            {name: 'activatorIp'},
                            {name: 'status'},
                            {name: 'openTime'},
                            {name: 'closeTime'},
                            {name: 'closeTimenf'},
                            {name: 'duration'},
                            {name: 'eventLogId'}
                        ]);

                        var len = 0;
                        var dataMap = null;
                        var delRecord = null;
                        var eventLogId;
                        $.each(arrayList, function(i) {
                            dataMap = arrayList[i];
                            delRecord = null;
                            eventLogId = dataMap.eventLogId;

                            //추가 데이터 정의
                            var record = new recordFrame({
                                    eventMessage : dataMap.eventMessage ,
                                    location : dataMap.location,
                                    activatorId : dataMap.activatorId,
                                    activatorType : dataMap.activatorType,
                                    activatorIp : dataMap.activatorIp,
                                    status : dataMap.status,
                                    openTime : dataMap.openTime,
                                    closeTime : dataMap.closeTime,
                                    closeTimenf : dataMap.closeTimenf,
                                    duration : dataMap.duration,
                                    eventLogId : eventLogId
                            });

                            len = EventAlertLogRealTimeMaxStore.getCount();

                            if (len > 0) {
                                for (var i = 0; i < len; i++) {
                                    delRecord = EventAlertLogRealTimeMaxStore.getAt(i);
                                    if (eventLogId == delRecord.data.eventLogId) {
                                        EventAlertLogRealTimeMaxStore.remove(delRecord);
                                        //len--;
                                        break;
                                    }
                                }
                            }

                            //store에 실시간 데이타 insert
                            EventAlertLogRealTimeMaxStore.insert(0, record);
                            //len++;
                        });
                        getEventAlertLogRealTimeMax();
                        //EventAlertLogRealTimeMaxStore.reload();
                    }

                },
                complete:function(xhr, status) {
                    pollMessage();
                        
                }       
            });
        } */

        // 실시간정보 가져오기 시작. Topic 가져오는 Thread 실행.
        /* function initMessage() {
            $.ajax({
                type : 'GET',
                data : {
                    clientId : clientId
                },
                url : '${ctx}/gadget/device/eventAlert/getRealTimeTopicData.do',
                success: function () {
                    pollMessage();
                }    
            });
        } */

        var dummyArray = new Array();
        var EventAlertLogRealTimeMaxModel;
        var EventAlertLogRealTimeMaxPanel;
        var EventAlertLogRealTimeMaxStore = null;
        var EventAlertLogRealTimeMaxInstanceOn = false;
        //EventAlertLogRealTimeMax  grid fetch func
        function getEventAlertLogRealTimeMax() {
            var panelHeight = 500;
            var width = $("#EventAlertLogRealTimeMaxDiv").width();

            if (width <= 0) {
                width = gridWidth;
            } else {
                gridWidth = width;
            }

            if (EventAlertLogRealTimeMaxInstanceOn == false) {
                EventAlertLogRealTimeMaxStore = new Ext.data.JsonStore({
                    autoLoad : true,
                    data : dummyArray,
                    fields : [
                              "eventMessage"
                             ,"location"
                             ,"activatorId"
                             ,"activatorType"
                             ,"status"
                             ,"writeTime"
                             ,"openTime"
                             ,"closeTime"
                             ,"closeTimenf"
                             ,"duration"
                             ,"eventLogId"
                    ],
                    listeners : {
                    	add : function(store, rec, ind){
                    		var count = store.getCount();
                    		//50개 이상의 레코드가 생성되면 하나씩 삭제.
                    		if(count > 50){
                    			store.removeAt(count-1);
                    		}
                    	}
                    }
                });
            }

            var fmtMessage = getFmtMessage();

            var colWidth = (width/8) - chromeColAdd;
            
            dateFormat=function(val) {
            	if(val.length==14){
            		yyyy = val.substr(0,4);
            		mm = val.substr(4,2);
            		dd = val.substr(6,2);
            		hh = val.substr(8,2);
            		nn = val.substr(10,2);
            		ss = val.substr(12,2);
            		return yyyy + "-" + mm + "-" + dd + " *" + hh + ":" + nn + ":" + ss;
            	}else
            		return val;
            }

            nullcheck=function(val){
                if(val.toLowerCase()=='null') {
                    return '';
                }else{
                    return val;
                }
            }

            // EventAlertLogRealTimeMax Model define.
            EventAlertLogRealTimeMaxModel = new Ext.grid.ColumnModel({
                columns: [
                     {header: fmtMessage[0], dataIndex: 'eventMessage',  tooltip: fmtMessage[0], width: colWidth+100, align: "left"}
                    ,{header: fmtMessage[1], dataIndex: 'location',      tooltip: fmtMessage[1], width: colWidth-20,  align: "left"}
                    ,{header: fmtMessage[2], dataIndex: 'activatorId',   tooltip: fmtMessage[2], width: colWidth,  align: "left"}
                    ,{header: fmtMessage[3], dataIndex: 'activatorType', tooltip: fmtMessage[3], width: colWidth}
                    ,{header: fmtMessage[5], dataIndex: 'status',        tooltip: fmtMessage[5], width: colWidth-80}
                    ,{header: fmtMessage[6], dataIndex: 'writeTime',     tooltip: fmtMessage[6], width: colWidth, renderer : nullcheck}
                    ,{header: fmtMessage[7], dataIndex: 'openTime',      tooltip: fmtMessage[7], renderer : dateFormat}
                    ,{header: fmtMessage[8], dataIndex: 'closeTime',     tooltip: fmtMessage[8], width: colWidth, renderer : nullcheck}
                    ,{header: fmtMessage[9], dataIndex: 'duration',      tooltip: fmtMessage[9], renderer : nullcheck}
                ],
                defaults: {
                    sortable: false
                   ,menuDisabled: true
                   ,width: colWidth
                   ,align: "center"
                   ,renderer: addTooltip
               }
            });

            if (EventAlertLogRealTimeMaxInstanceOn == false) {
                //RealTimeMax grid panel 인스턴스 생성및 정의.
                EventAlertLogRealTimeMaxPanel = new Ext.grid.GridPanel({
                    store : EventAlertLogRealTimeMaxStore,
                    colModel : EventAlertLogRealTimeMaxModel,
                    sm : new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll : false,
                    scroll : false,
                    width : width,
                    height : panelHeight,
                    stripeRows : true,
                    columnLines : true,
                    renderTo : 'EventAlertLogRealTimeMaxDiv',
                    viewConfig : {
                        forceFit : true,
                        enableRowBody : true,
                        showPreview : true,
                    },                    
                    listeners : {
                        rowcontextmenu : function(grid, index, event) {
                            if (editAuth == "true") {
                                //grid.selectedNode = grid.store.getAt(index);  // we need this
                                if (index != null) {
                                    this.getSelectionModel().selectRow(index);
                                }

                                showRealtimeGridMenu(grid, index, event);
                            }
                        }
                    }
                });
                EventAlertLogRealTimeMaxInstanceOn = true;
            } else {
                EventAlertLogRealTimeMaxPanel.setWidth(width);
                EventAlertLogRealTimeMaxPanel.reconfigure(EventAlertLogRealTimeMaxStore, EventAlertLogRealTimeMaxModel);
            }

            hide();
        };//func EventAlertLogRealTimeMaxList End

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
                                                           EventAlertLogRealTimeMaxStore.remove(record);
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
            var len = EventAlertLogRealTimeMaxStore.getCount();

            if (len > 0) {
                var delRecord = null;
                var closeTime = null;
                var date = null;
                var rdate = null;
                var cdate = null;

                for (var i = 0; i < len; i++) {
                    delRecord = EventAlertLogRealTimeMaxStore.getAt(i);
                    status = delRecord.data.status;
                    
                    if (status == "Cleared" || status == "ClearedManually") {
                        closeTime = delRecord.data.closeTimenf;
                        date = new Date(closeTime.substring(0, 4), (closeTime.substring(4, 6)-1), closeTime.substring(6, 8),
                                        closeTime.substring(8, 10), closeTime.substring(10, 12), closeTime.substring(12, 14));

                        rdate = new Date();
                        rdate.setTime(date.getTime() + gridRemoveInterval);

                        cdate = new Date();

                        if (cdate > rdate) {
                            EventAlertLogRealTimeMaxStore.remove(delRecord);
                            i--;
                            len--;
                        }
                    }
                }
            }
        }

        //탭초기화
        var tabs = {hourly:0,period:0,monthlyPeriod:0,yearly:0};
        var tabNames = {};

        var currentTab;

        function changeTab(type) {
            var realTimeTab = document.getElementsByName('realTimeTab');
            var historyTab = document.getElementsByName('historyTab');
            var configurationTab = document.getElementsByName('configurationTab');
            //var settingTab = document.getElementsByName('settingTab');
            var thresholdTab = document.getElementsByName('thresholdTab');	// INSERT SP-193

            if (type == "realTime") {
                realTimeTab[0].id = "current";
                historyTab[0].id = "";
                configurationTab[0].id = "";
                thresholdTab[0].id = "";		// INSERT SP-193
                getRealTime();
            } else if (type == "history") {
                realTimeTab[0].id = "";
                historyTab[0].id = "current";
                configurationTab[0].id = "";
                thresholdTab[0].id = "";		// INSERT SP-193
                getHistory();
            } else if (type == "configuration"){
            	realTimeTab[0].id = "";
                historyTab[0].id = "";
                configurationTab[0].id = "current";
                thresholdTab[0].id = "";		// INSERT SP-193
                getConfiguration();
            } 
            // INSERT START SP-193
            else if (type == "threshold"){
            	realTimeTab[0].id = "";
                historyTab[0].id = "";
                configurationTab[0].id = "";
                thresholdTab[0].id = "current";
                getThreshold();
            }
            // INSERT END SP-193
            
            
        }

        function changeHistoryTab(type) {
//          var logTab = document.getElementsByName('logTab');
//          var typeTab = document.getElementsByName('typeTab');
//          var msgTab = document.getElementsByName('msgTab');

            if (type == "log") {
//              logTab[0].id = "current";
//              typeTab[0].id = "";
//              msgTab[0].id = "";

                document.getElementById("EventAlarmLogGridDiv").style.display = "block";
                document.getElementById("pane-history-type").style.display = "none";
                document.getElementById("pane-history-msg").style.display = "none";
            } else if (type == "type") {
//              logTab[0].id = "";
//              typeTab[0].id = "current";
//              msgTab[0].id = "";

                document.getElementById("EventAlarmLogGridDiv").style.display = "none";
                document.getElementById("pane-history-type").style.display = "block";
                document.getElementById("pane-history-msg").style.display = "none";
                updateFChart();
            } else if (type == "msg") {
//              logTab[0].id = "";
//              typeTab[0].id = "";
//              msgTab[0].id = "current";

                document.getElementById("EventAlarmLogGridDiv").style.display = "none";
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
                        target.options.add(new Option(severity['descr'], severity['name']), index+1);

                        if (profile != null && profile['severity'] == severity['descr']) {
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
                        target.options.add(new Option(status['descr'], status['name']), index+1);

                        if (profile != null && profile['status'] == status['descr']) {
                            target.selectedIndex = index + 1;
                        }
                    });

                    target = document.getElementById("activatorType");
                    target.options.length = 0;
                    target.options.add(new Option("<fmt:message key='aimir.all'/>", "all"), 0);
                    $.each(json.activatorType, function(index, activatorType) {
                        target.options.add(new Option(activatorType['descr'], activatorType['name']), index+1);

                        if (profile != null && profile['activatorType'] == activatorType['descr']) {
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
        
        // Configuration Tab - Event List
        var eventAlertListGrid;
        var eventAlertListColModel;
        var eventAlertListStore;
        var eventAlertListInstanceOn=true;
        function getEventAlertList() {
        	var width = $("#eventAlertListGrid").width();        	
        	var gridWidth = width;
        	var rowSize = 10;
        	
        	// Store
        	eventAlertListStore = new Ext.data.JsonStore({
        		autoLoad : {params:{start: 0, limit: rowSize}},
        		url : '${ctx}/gadget/device/eventAlert/getEventAlertList.do',
        		baseParams : {
        			pageSize : rowSize,
        		},
        		totalProperty : 'eventAlertListCount',
        		root : 'eventAlertRoot',
        		fields : [
        		          "id",
        		          "name",
        		          "descr",
        		          "eventAlertType",
        		          "monitor",
        		          "severity",
        		          "troubleAdvice"
        		          ],
        		listeners : {
        			beforeload : function(store, options) {
        				options.params || (options.params = {});
        				Ext.apply(options.params, {
        					page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)
        				});
        			}
        		}
        	}); // --Store
        	
        	var colWidth = (width-50)/11-chromeColAdd;
        	// Column Model
        	eventAlertListColModel = new Ext.grid.ColumnModel({
        		columns : [
        		           {header: "CATEGORY",			dataIndex: 'eventAlertType',width: 45},
        		           {header: "NAME",				dataIndex: 'name',},
        		           {header: "DESCRIPTION",		dataIndex: 'descr',},
        		           {header: "MONITOR TYPE",		dataIndex: 'monitor',		width: 80},
        		           {header: "SEVERITY LEVEL",	dataIndex: 'severity',		width: 70},
        		           {header: "TROUBLE ADVICE",	dataIndex: 'troubleAdvice',}
        		           ],
        		defaults: {
        			sortable: true,
        			menuDisabled: true,
        			width: colWidth,
        			align: "center"        		
        		}           
        	}); // --Column Model
        	
        	if(eventAlertListInstanceOn){
        		$("#eventAlertListGrid").html('');
        		// Grid Panel
            	eventAlertListGrid = new Ext.grid.GridPanel({
            		store : eventAlertListStore,
            		colModel : eventAlertListColModel,
            		sm : new Ext.grid.RowSelectionModel({
            			singleSelect:true,
            			listeners: {
                            rowselect: function(smd, row, rec) {
                            	var data = rec.data;
                                //선택한 항목에 해당하는 내용 출력
                                eventItemSelected(data);
                            }
                        }
            		}),
            		autoScroll : false,
            		scroll : false,
            		width : gridWidth,
            		height : 288,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg: 'loading...'
                    },
                    renderTo : 'eventAlertListGrid',
                    viewConfig : {
                    	forceFit:true,
                        scrollOffset: 1,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText: '<fmt:message key="aimir.extjs.empty"/>',
                    },
                    // paging bar on the bottom
                    bbar : new Ext.PagingToolbar({
                        pageSize : rowSize,
                        store : eventAlertListStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
                    }),                    
                });
            	eventAlertListInstanceOn = false;
        	}else {
        		eventAlertListGrid.setWidth(gridWidth);
                var bottomToolbar = eventAlertListGrid.getBottomToolbar();
                eventAlertListGrid.reconfigure(eventAlertListStore, eventAlertListColModel);
                bottomToolbar.bindStore(eventAlertListStore);
        	}
        	        
        	
        }
        
        // Configuration 탭의 Editing 패널 초기화
        function editingInit() {
        	if(editAuth == "false"){
        		$('#eventBasicInfo').css('display','none');
        		$('#eventAlertEditPane').html('<p><b>Invalid Auth</b></p>');
        		return;
        	}
        	
        	// Category의 selectbox 초기화
        	var elemt = document.getElementById("tdCategory");
        	elemt.options.length = 0;
        	elemt.options.add(new Option("<fmt:message key='aimir.select.row.no'/>", "empty"),0);
        	$('#tdCategory').selectbox();
        	// Severity Level의 selectbox 초기화
        	var elemt = document.getElementById("tdSeverity");
        	elemt.options.length = 0;
        	elemt.options.add(new Option("<fmt:message key='aimir.select.row.no'/>", "empty"),0);
        	$('#tdSeverity').selectbox();
        	// Monitor Type의 selectbox 초기화 
        	elemt = document.getElementById("tdMonitor");
        	elemt.options.length = 0;
        	elemt.options.add(new Option("<fmt:message key='aimir.select.row.no'/>", "empty"),0);
        	$('#tdMonitor').selectbox();
        	// input box 초기화
        	var tdWidth = $('#eventBasicInfo').width()/2;
        	$('#tdDescr').width(tdWidth);
        	$('#tdAdvice').width(tdWidth);
        	$('#tdDescr').val('');
        	$('#tdAdvice').val('');
        	// Label 초기화
        	$('#eventNameLabel').html('<fmt:message key="aimir.device.eventName"/>');        	
        	// 버튼 출력 전환
        	$('#tdApply').css('display','none');
        	$('#tdCancel').css('display','none');
        	$('#tdUpdate').css('display','');
        }
        
        // eventAlertListGrid 아이템 선택 이벤트
        function eventItemSelected(data) {
        	// selectbox option 설정
        	$.getJSON('${ctx}/gadget/device/eventAlert/eventAlertConfInit.do',
				function(json) {
					var target;
					var profile = null;
					
					if(json.profile != null) {
						$.each(json.profile, function(index, data){
							profile = data;
						});
					}
					// category 설정
					target = document.getElementById("tdCategory");
					target.options.length = 0;				
					$.each(json.category, function(index, category) {
						target.options.add(new Option(category['name'], category['name']), index+1);
						if(profile != null && profile['category'] == category['descr']){
							target.selectedIndex = index+1;
						}
					});	
					$('#tdCategory').selectbox();
					// severity 설정
					target = document.getElementById("tdSeverity");
					target.options.length = 0;				
					$.each(json.severity, function(index, severity) {
						target.options.add(new Option(severity['name'], severity['name']), index+1);
						if(profile != null && profile['severity'] == severity['descr']){
							target.selectedIndex = index+1;
						}
					});	
					$('#tdSeverity').selectbox();
					// monitor type 설정
					target = document.getElementById("tdMonitor");
					target.options.length = 0;				
					$.each(json.monitor, function(index, monitor) {
						target.options.add(new Option(monitor, monitor), index+1);					
					});
					$('#tdMonitor').selectbox();
					
					// 버튼초기화
		        	$('#tdUpdate').css('display','');
		            $('#tdApply').css('display','none');
		            $('#tdCancel').css('display','none');
		        	// 선택된 이벤트의 설정값 출력
		        	$('#eventNameLabel').html('<fmt:message key="aimir.device.eventName"/> : '+ data.name);
		        	$('#tdCategory').val(data.eventAlertType.trim()).attr("selected", "selected");
		        	$('#tdCategory').selectbox();
		        	$('#tdMonitor').val(data.monitor.trim()).attr("selected", "selected");
		        	$('#tdMonitor').selectbox();
		        	$('#tdSeverity').val(data.severity.trim()).attr("selected", "selected");        	
		        	$('#tdSeverity').selectbox();
		        	$('#tdDescr').val(data.descr);
		        	$('#tdAdvice').val(data.troubleAdvice);
		        	$('#eventIdHiddenLabel').val(data.id);
        	});
        	
        	
        	// 입력칸 상태 전환        	        	
        	//$('#tdDescr').addClass("bg-blue");
        	//$('#tdAdvice').addClass("bg-blue");             	              	
        }
        
        // Editing 패널 값 수집
        function checkEditingForm(){
        	var arrayObj = Array();
        	//cache
        	arrayObj[0] = Math.random();
        	//values
        	arrayObj[1] = $('#eventIdHiddenLabel').val().trim();
        	arrayObj[2] = $('#tdMonitor').val().trim();
        	arrayObj[3] = $('#tdSeverity').val().trim();
        	arrayObj[4] = $('#tdDescr').val().trim();
        	arrayObj[5] = $('#tdAdvice').val().trim();
        	arrayObj[6] = $('#tdCategory').val().trim();
        	return arrayObj;
        }
        
        // Editing 패널 버튼 이벤트
        function editingButtonClick(eBtn){
        	if(eBtn.trim() == 'Update'){
        		// 버튼 출력 전환
        		$('#tdUpdate').css('display','none');
            	$('#tdApply').css('display','');
            	$('#tdCancel').css('display','');
            	// 입력칸 입력가능하도록 전환
            	//$('#tdDescr').removeClass("bg-blue");
        		//$('#tdAdvice').removeClass("bg-blue");            	
        	}else if(eBtn.trim() == 'Apply'){
        		var conditionArray = checkEditingForm();
        		// Confirm 
        		Ext.Msg.confirm('<fmt:message key="aimir.warning"/>', '<fmt:message key="aimir.update.want" />', 
	    			function(btn,text){
			    		if(btn == 'yes'){ 
			    			// 변경 내용 적용
			    			$.ajax({
			    				type : "POST",
			    				data : {
			    					'eId' : conditionArray[1],
			    					'eMonitorType' : conditionArray[2],
			    					'eSeverity' : conditionArray[3],
			    					'eDescription' : conditionArray[4],
			    					'eAdvice' : conditionArray[5],
			    					'eCategory' : conditionArray[6]
			    				},
			    				dataType : "json",
			    				url : '${ctx}/gadget/device/eventAlert/updateEventConfiguration.do',
			    				success : function(data,status){
			    					Ext.Msg.alert(data.result.result,data.result.info);
			    					getEventAlertList();
			    				},
			    				error : function(xhr,status){
			    					Ext.Msg.alert('<fmt:message key="aimir.message"/>','Failed to Ajax Communication');
			    					getEventAlertList();
			    				}
			    			});
			    		}else {
			    			Ext.Msg.alert('<fmt:message key="aimir.warning"/>','<fmt:message key="aimir.canceled" />');
			    			getEventAlertList();
			    		}
			    	});        		
        		// 버튼 출력 전환
        		$('#tdUpdate').css('display','');
            	$('#tdApply').css('display','none');
            	$('#tdCancel').css('display','none');
        	}else if(eBtn.trim() == 'Cancel'){
        		// Editing Panel 초기화
        		editingInit();        		
        	}else ;
        }
        
        function resetData() {
            document.getElementById("eventAlertType").selectedIndex = 0;
            document.getElementById("severity").selectedIndex = 0;
            document.getElementById("eventAlertClass").selectedIndex = 0;
            document.getElementById("status").selectedIndex = 0;
            document.getElementById("activatorType").selectedIndex = 0;
            document.getElementById("activatorId").value = "";
//          document.getElementById("location").selectedIndex = 0;
            document.getElementById("message").value = "";
        }

        //#검색 버튼 클릭시 발생하는 event
        /* function searchHistory() {
            var condition = getCondition();

            updateFChart();

            //extjs grid chart call
            getEventAlarmLogGrid();


            //플렉스 그리드 차트 호출.
            getFlexObject('EventAlertLogHistory').searchData(condition);
        } */

        function searchHistory() {
        	updateFChart();

            //extjs grid chart call
            getEventAlarmLogGrid();
        }

        /* function saveExcel() {
            getFlexObject('EventAlertLogHistory').saveExcel();
        } */

        function getCondition3() {
            var condition = new Array();


            var conditionCnt = 0;
            var supplierId = "${supplierId}";

            if (supplierId != null) {
                condition[conditionCnt] = "supplier:" + supplierId;
                conditionCnt++;
            }

        }

        function getCondition() {
            var condition = new Array();

            var conditionCnt = 0;
            var supplierId = "${supplierId}";

            if (supplierId != null) {
                condition[conditionCnt] = "supplier:" + supplierId;
                conditionCnt++;
            }
            
            var now = new Date();
            
            condition[conditionCnt++] = "location:" + $('#location').val();

            if($('#searchStartDate').val() == null || $('#searchStartDate').val() == "") {
            	$('#searchStartDate').val(now.getFullYear().toString() + (now.getMonth()+1).toString() + now.getDate().toString());
            }
            
			if($('#searchEndDate').val() == null || $('#searchEndDate').val() == "") {
				$('#searchEndDate').val(now.getFullYear().toString() + (now.getMonth()+1).toString() + now.getDate().toString());
            }

            $('#searchStartDate').val($('#searchStartDate').val().replace(/\//g,''));
            $('#searchEndDate').val($('#searchEndDate').val().replace(/\//g,''));
            
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

        <%--function saveProfile(tab) {
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
                for (i = 0; i < fieldErrors.length; i++) {
                    var temp = '#'+fieldErrors[i].objectName+' :input[name=\"'+fieldErrors[i].field+'\"]';
                    $(temp).val(''+fieldErrors[i].defaultMessage);
                }
            }
        }--%>

		// INSERT START SP-193
        function getThresholdList() {
        	$.getJSON('${ctx}/gadget/device/eventAlert/getThresholdList.do',
				function(json) {
					var target;
					var profile = null;
					var count=0;
					var nameValue;
					
					if(json.thresholdList != null) {
						$.each(json.thresholdList, function(index, data){
//							$.each(json.thresholdNameList, function(subidx, subdata){
//								if (data['name'] == subdata['name']) {
//									nameValue = subdata['value'];
//									break;
//								} 
//							});
							for (var i = 0; i < json.thresholdNameList.length; i++) {
								if (data['name'] == json.thresholdNameList[i]['name']) {
									nameValue = json.thresholdNameList[i]['value'];
									break;
								} 							
							}
							document.getElementById('kind' + (index+1)).value= nameValue;
							document.getElementById('threshold' + (index+1)).value= data['limit'];
							//document.getElementById('schedule' + (index+1)).value= data['duration'];
							count++;
						});
						document.getElementById('thresholdCount').value = count;
					}
        	});
        	        	
        }

        function checkThresholdTab(){
        	var arrayObj = new Array();

        	arrayObj[0] = new Array(); // name
        	arrayObj[1] = new Array(); // threshold
        	arrayObj[2] = new Array(); // schedule
        	
        	var count = $('#thresholdCount').val();

        	for (i = 0; i < count; i++) {
	        	arrayObj[0][i] = document.getElementById('kind' + (i+1)).value;
	        	arrayObj[1][i] = document.getElementById('threshold' + (i+1)).value;
	        	//arrayObj[2][i] = document.getElementById('schedule' + (i+1)).value;
	        	arrayObj[2][i] = "";
        	}
        	
        	return arrayObj;
        }
        
        function saveButtonClick(){
        	
        	var paramArray = checkThresholdTab();
        	var nameArray = paramArray[0].map(function(el){return String(el);});
        	var thresholdArray = paramArray[1].map(function(el){return String(el);});
        	var scheduleArray = paramArray[2].map(function(el){return String(el);});
        	
        	
			    			$.ajax({
			    				type : "POST",
			    				data : {
			    					'count' : paramArray[0].length,
			    					'name' : nameArray,
			    					'threshold' : thresholdArray,
			    					'schedule' : scheduleArray
			    				},
			    				dataType : "json",
			    				url : '${ctx}/gadget/device/eventAlert/updateAllThreshold.do',
			    				success : function(data,status){
			    					Ext.Msg.alert(data.result.result,data.result.info);
			    					getEventAlertList();
			    				},
			    				error : function(xhr,status){
			    					Ext.Msg.alert('<fmt:message key="aimir.message"/>','Failed to Ajax Communication');
			    					getEventAlertList();
			    				}
			    			});
        }		
		
		// INSERT END SP-193
        
        function getRealTime() {
            //document.getElementById("pane-setting").style.display = "none";
            document.getElementById("searchoption-container").style.display = "none";
            document.getElementById("pane-condition").style.display = "block";
            //document.getElementById("pane-condition-realtime").style.display = "block";
            //document.getElementById("pane-condition-history").style.display = "none";
            document.getElementById("pane-datecondition-history").style.display = "none";

            document.getElementById("pane-data-realtime").style.display = "block";
            document.getElementById("pane-data-history").style.display = "none";
            document.getElementById("pane-data-configuration").style.display = "none";
            
            document.getElementById("pane-data-threshold").style.display = "none";	// INSERT SP-193
            

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
            document.getElementById("pane-data-configuration").style.display = "none";

            document.getElementById("pane-data-threshold").style.display = "none";	// INSERT SP-193
            
            resetData();
            document.getElementById("logTab").checked = true;
            changeHistoryTab("log");

            //show grid chart
            getEventAlarmLogGrid();
        }
        
        function getConfiguration() {
        	document.getElementById("searchoption-container").style.display = "none";
        	document.getElementById("pane-condition").style.display = "none";
        	        	
        	document.getElementById("pane-data-realtime").style.display = "none";
            document.getElementById("pane-data-history").style.display = "none";            
            document.getElementById("pane-data-configuration").style.display = "block";

            document.getElementById("pane-data-threshold").style.display = "none";	// INSERT SP-193
            
            // show event,alert list
            getEventAlertList();
            
            // init
            editingInit();            
        }

		// INSERT START SP-193        
        function getThreshold() {
        	document.getElementById("searchoption-container").style.display = "none";
        	document.getElementById("pane-condition").style.display = "none";
        	        	
        	document.getElementById("pane-data-realtime").style.display = "none";
            document.getElementById("pane-data-history").style.display = "none";            
            document.getElementById("pane-data-configuration").style.display = "none";

            document.getElementById("pane-data-threshold").style.display = "block";
            
            // show threshold settings
            getThresholdList();
            
            // init
        }        
		// INSERT END SP-193        

        function getFmtMessage(){
            var fmtMessage = new Array();

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
            fmtMessage[12] = "<fmt:message key='aimir.firmware.msg09'/>";
            fmtMessage[13] = "<fmt:message key='aimir.excel.eventalterlog'/>";

            return fmtMessage;
        }

        function getCondition1() {
            var array = [];
            array[0] = '${supplierId}';
            array[1] = $('#searchStartDate').val();
            array[2] = $('#searchEndDate').val();

            return array;
        }
        //EventAlertLogHistory

        function updateFChart() {
            emergePre();

            $.getJSON('${ctx}/gadget/device/eventAlert/getEventAlertLog.do'
                    ,{supplierId : '${supplierId}',
                      searchStartDate : $('#searchStartDate').val(),
                      searchEndDate : $('#searchEndDate').val(),
                      eventAlertType : ($('#eventAlertType').val() == null || $('#eventAlertType').val() == "all") ? "" : $('#eventAlertType').val(),
                      severity : ($('#severity').val() == null || $('#severity').val() == "all") ? "" : $('#severity').val(),
                      eventAlertClass : ($('#eventAlertClass').val() == null || $('#eventAlertClass').val() == "all") ? "" : $('#eventAlertClass').val(),
                      status : ($('#status').val() == null || $('#status').val() == "all") ? "" : $('#status').val(),
                      activatorType : ($('#activatorType').val() == null || $('#activatorType').val() == "all") ? "" : $('#activatorType').val(),
                      activatorId : $('#activatorId').val(),
                      locationId : $('#location').val(),
                      message : $('#message').val()}
                    ,function(json) {
                         var logTypeList = json.LogType;
                         fcChartLogTypeDataXml = "<chart "
                            + "chartLeftMargin='10' "
                            + "chartRightMargin='10' "
                            + "chartTopMargin='10' "
                            + "chartBottomMargin='10' "
                            + "showValues='0' "
                            + "numberSuffix=' ' "
                            + "yaxismaxvalue='5' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_Column3D_nobg
                            + ">";
                         var logTypeLabels = new Array();

                         if (logTypeList == null || logTypeList.length == 0) {
                             logTypeLabels.push("<set label='' value='0' color='E48701'/>");
                         } else {
                             var logTypeListLen = logTypeList.length;
                             for (var i = 0; i < logTypeListLen; i++) {
                            	 logTypeLabels.push("<set label='"+logTypeList[i].type+"' value='"+logTypeList[i].value+"' color='E48701'/>");
                             }
                         }

                         fcChartLogTypeDataXml += logTypeLabels.join("") + "</chart>";

                         var logMessageList = json.LogMessage;
                         fcChartLogMessageDataXml = "<chart "
                            + "chartLeftMargin='10' "
                            + "chartRightMargin='10' "
                            + "chartTopMargin='10' "
                            + "chartBottomMargin='10' "
                            + "showValues='0' "
                            + "useRoundEdges='0' "
                            + "numberSuffix=' ' "
                            + "xaxismaxvalue='5' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_Column2D_nobg
                            + ">";
                         var logMessageLabels = new Array();

                         if (logMessageList == null || logMessageList.length == 0) {
                             logMessageLabels.push("<set label='' value='0' color='E48701'/>");
                         } else {
                             var logMessageListLen = logMessageList.length;
                             var color= 'E48701'; //defualt
                             for (var i = 0; i < logMessageListLen; i++) {
                            	 //severity에 따라 chart label색상 변경 
                                 switch(logMessageList[i].severity){
                                 	case "Critical" : color = red; break;
                                 	case "Major" : color = orange; break;
                                 	case "Minor" : color = yellow; break;
                                 	case "Warning" : color = green; break;
                                 	case "Information" : color = blue; break;
                                 	case "Normal" : color = purple; break;
                                 } 
                                 logMessageLabels.push("<set label='"+logMessageList[i].type+"' value='"+logMessageList[i].value + "' color='"+color+"'/>");
                             }
                         }
                         fcChartLogMessageDataXml += logMessageLabels.join("") + "</chart>";

                         fcChartRender();

                         hide();
                    }
            );
        }

        window.onresize = fcChartRender;
        function fcChartRender() {
            var width = 0;
            if ($('#fcChartLogTypeDiv').width() > 0) width = $('#fcChartLogTypeDiv').width();
            else width = $('#fcChartLogMessageDiv').width();

            if ($('#fcChartLogTypeDiv').is(':visible')) {
        		fcChartLogType = new FusionCharts({
					type: 'Column3D',
					renderAt : 'fcChartLogTypeDiv',
					width : width,
					height : '300',
					dataSource : fcChartLogTypeDataXml
				}).render();
                /* fcChartLogType = new FusionCharts("${ctx}/flexapp/swf/fcChart/Column3D.swf", "fcChartLogTypeId", width, "300", "0", "0");
                fcChartLogType.setDataXML(fcChartLogTypeDataXml);
                fcChartLogType.setTransparent("transparent");
                fcChartLogType.render("fcChartLogTypeDiv"); */
            }

            if ($('#fcChartLogMessageDiv').is(':visible')) {
        		fcChartLogMessage = new FusionCharts({
					type: 'Bar2D',
					renderAt : 'fcChartLogMessageDiv',
					width : width,
					height : '300',
					dataSource : fcChartLogMessageDataXml
				}).render();
                //fcChartLogMessage = new FusionCharts("${ctx}/flexapp/swf/fcChart/Bar2D.swf", "fcChartLogMessageId", width, "300", "0", "0");
                //fcChartLogMessage.setDataXML(fcChartLogMessageDataXml);
                //fcChartLogMessage.setTransparent("transparent");
                //fcChartLogMessage.render("fcChartLogMessageDiv");
            }
        }

        //report window(Excel)
        var winEventAlertLog;
        function openExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            obj.condition   = getCondition();
            obj.fmtMessage  = getFmtMessage();

            if(winEventAlertLog)
                winEventAlertLog.close();
            winEventAlertLog = window.open("${ctx}/gadget/device/eventAlert/eventAlertLogExcelDownloadPopup.do", "EventAlertLogExcel", opts);
            winEventAlertLog.opener.obj = obj;
        }

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
        
        // INSERT START SP-193  
        var helpWin;
        var winH = 1000;
        var winW = 700;
        var helpColModel;
        function makeHelpWindow() {
            var html = '<div id="wrap" >'
                + '<h2><label class="check">Format</label></h2>'
                + '<p>'
                + 'A cron expression is a string comprised of 6 or 7 fields separated by white space. Fields can contain any of the'
                + 'allowed values, along with various combinations of the allowed special characters for that field. The fields are as'
                + 'follows:'
                + '</p>'
                + '<table class="help_cronExpression">'
                + '    <tr>'
                + '        <th>Field Name</th>'
                + '        <th>Mandatory</th>'
                + '        <th>Allowed Values</th>'
                + '        <th>Allowed Special Characters</th>'
                + '    </tr>'
                + '    <tr>'
                + '        <td>Seconds</td>'
                + '        <td>YES</td>'
                + '        <td width="30%">0-59</td>'
                + '        <td width="40%">, - * /</td>'
                + '    </tr>'
                + '    <tr >'
                + '        <td>Minutes</td>'
                + '        <td>YES</td>'
                + '        <td>0-59</td>'
                + '        <td>, - * /</td>'
                + '    </tr>'
                + '    <tr height="15px">'
                + '        <td>Hours</td>'
                + '        <td>YES</td>'
                + '        <td>0-23</td>'
                + '        <td>, - * /</td>'
                + '    </tr>'
                + '    <tr height="15px">'
                + '        <td>Day of month</td>'
                + '        <td>YES</td>'
                + '        <td>1-31</td>'
                + '        <td>, - * ? / L W'
                + '        </td>'
                + '    </tr>'
                + '    <tr height="15px">'
                + '        <td>Month</td>'
                + '        <td>YES</td>'
                + '        <td>1-12 or JAN-DEC</td>'
                + '        <td>, - * /</td>'
                + '    </tr>'
                + '    <tr height="15px">'
                + '       <td>Day of week</td>'
                + '       <td>YES</td>'
                + '       <td>1-7 or SUN-SAT</td>'
                + '       <td>, - * ? / L #</td>'
                + '    </tr>'
                + '    <tr  height="15px">'
                + '        <td>Year</td>'
                + '        <td>NO</td>'
                + '        <td>empty, 1970-2099</td>'
                + '        <td>, - * /</td>'
                + '    </tr>'
                + ' </table>' 
                + '    <p>So cron expressions can be as simple as this: <tt>* * * * ? *</tt></p>'
                + '    <p>or more complex, like this: <tt>0/5 14,18,3-39,52 * ? JAN,MAR,SEP MON-FRI 2002-2010</tt></p>'
                + '    <h2><label class="check">Examples</label></h2>'
                + '    <p>Here are some full examples:</p>'
                + '    <table class="help_cronExpression">'
                + '            <tr>'
                + '                <th width="150">Expression</th>'
                + '                <th>Meaning</th>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 0 12 * * ?</td>'
                + '                <td>Fire at 12pm (noon) every day</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 ? * *</td>'
                + '                <td>Fire at 10:15am every day</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 * * ?</td>'
                + '                <td>Fire at 10:15am every day</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 * * ? *</td>'
                + '                <td>Fire at 10:15am every day</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 * * ? 2005</td>'
                + '                <td>Fire at 10:15am every day during the year 2005</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 * 14 * * ?</td>'
                + '                <td>Fire every minute starting at 2pm and ending at 2:59pm, every day</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 0/5 14 * * ?</td>'
                + '                <td>Fire every 5 minutes starting at 2pm and ending at 2:55pm, every day</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 0/5 14,18 * * ?</tt></td>'
                + '                <td>Fire every 5 minutes starting at 2pm and ending at 2:55pm, AND fire every 5'
                + '                minutes starting at 6pm and ending at 6:55pm, every day</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 0-5 14 * * ?</td>'
                + '                <td>Fire every minute starting at 2pm and ending at 2:05pm, every day</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 10,44 14 ? 3 WED</td>'
                + '                <td>Fire at 2:10pm and at 2:44pm every Wednesday in the month of March.</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 ? * MON-FRI</td>'
                + '                <td>Fire at 10:15am every Monday, Tuesday, Wednesday, Thursday and Friday</td>'
                + '            </tr>'
                + '            <tr>'
                + '               <td>0 15 10 15 * ?</td>'
                + '                <td>Fire at 10:15am on the 15th day of every month</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 L * ?</td>'
                + '                 <td>Fire at 10:15am on the last day of every month</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 ? * 6L</td>'
                + '                <td>Fire at 10:15am on the last Friday of every month</td>'
                + '            </tr>'
                + '            <tr>'
                + '               <td>0 15 10 ? * 6L</td>'
                + '                <td>Fire at 10:15am on the last Friday of every month</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 ? * 6L 2002-2005</td>'
                + '                <td>Fire at 10:15am on every last friday of every month during the years 2002,'
                + '                2003, 2004 and 2005</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 15 10 ? * 6#3</td>'
                + '                <td>Fire at 10:15am on the third Friday of every month</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 0 12 1/5 * ?</td>'
                + '                <td>Fire at 12pm (noon) every 5 days every month, starting on the first day of the'
                + '                month.</td>'
                + '            </tr>'
                + '            <tr>'
                + '                <td>0 11 11 11 11 ?</td>'
                + '                <td>Fire every November 11th at 11:11am.</td>'
                + '            </tr>'
                + '    </table>'
                + '    <blockquote>'
                + '            Pay attention to the effects of "?" and "*" in the day-of-week and day-of-month fields!'
                + '    </blockquote>'
                + '</div>';

            //윈도우 설정    
            if (!helpWin) {
                helpWin = new Ext.Window({
                    layout: 'anchor',
                    title : 'How to set schedule ',
                    id : 'helpWinId',
                    applyTo : 'helpDiv',
                    modal: false,
                    autoScroll : true,
                    //autoHeight : true,
                    pageX : 400,
                    pageY : 10,
                    height: 500,
                    width: winW,
                    html: "<span style='line-height:20px; background-color:white;'>" + html + "</span>",
                    closeAction : 'hide',
                    constrain:true,
                    cls:'white'
                });
            }
            //imgWin.show(this);

            Ext.getCmp('helpWinId').show();
        }
        // INSERT END SP-193  
    /*]]>*/
    </script>
</head>
<body>

<!--상단탭-->
<div id="gad_sub_tab">
	<!-- INSERT SP-193 -->
    <div id='helpDiv'></div>     	

    <ul>
        <li><a href="javascript:changeTab('realTime')" name="realTimeTab" id="current"><fmt:message key='aimir.real.time'/></a></li>
        <li><a href="javascript:changeTab('history')" name="historyTab"><fmt:message key='aimir.history'/></a></li>
        <li><a href="javascript:changeTab('configuration')" name="configurationTab"><fmt:message key='aimir.config'/></a></li>
        <!--  <li><a href="javascript:changeTab('setting')" name="settingTab"><fmt:message key='aimir.setting'/></a></li>-->
		<!-- INSERT SP-193 -->
        <li><a href="javascript:changeTab('threshold')" name="thresholdTab"><fmt:message key='aimir.threshold'/></a></li>
    </ul>
</div>
<!--상단탭 끝-->

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
                        <!--  <td class="padding-r20px"><select name="eventAlertType" id="eventAlertType" onchange="javascript:getEventAlert();"></select></td> 
                        	# onchange 이벤트를 제거하여 아래에 재작성
                        -->
                        <td class="padding-r20px"><select name="eventAlertType" id="eventAlertType"></select></td>
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

        <!-- EventAlertLogRealTime Max Extjs  -->
        <div id="EventAlertLogRealTimeMaxDiv" style="display:block" class=""></div>

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
            </table>

        </div>
        <div class="margin-b3px textalign-right">
            <em class="am_button" >
               <a href="javascript:openExcelReport();" id="btnExcel"><fmt:message key="aimir.button.excel" /></a>
            </em>
        </div>

        <!-- extjs history-log GRID CHART DIV  -->
        <div id="EventAlarmLogGridDiv" style="display:block;" class="flexlist">

        </div>

        <div id="pane-history-type" style="display:none" class="flexlist">
            <div id="fcChartLogTypeDiv" >
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>
        <div id="pane-history-msg" style="display:none" class="flexlist">
            <div id="fcChartLogMessageDiv">
                The chart will appear within this DIV. This text will be replaced by the chart.
            </div>
        </div>
    </div>
    
    <div id="pane-data-configuration" class="gadget_body">
    	<div class="flexlist margin-t5px">
    	<label class="check margin-t5px">Event / Alert List</label>
    		<div id="eventAlertListGrid" class="margin-t5px" style="margin-bottom:20px;">
				The chart will appear within this DIV. This text will be replaced by the chart.
			</div>
		<label class="check margin-t5px">Editing</label>
    		<div id="eventAlertEditPane" class="box-bluegradation3 margin-t5px padding10px" >
    			<div id="eventBasicInfo">
    			<label id="eventNameLabel" class="blue11pt margin-t5px"><fmt:message key="aimir.device.eventName"/></label>
    			<label id="eventIdHiddenLabel" style="visible:hidden;"></label>
    				<table class="wfree">
    					<tr>
    						<td class="graybold11pt withinput">CATEGORY</td><td class="padding-r20px"><select id="tdCategory" name="tdCategory" style="width:150px"></select></td>
    					</tr><tr>
    						<td class="graybold11pt withinput">MONITOR TYPE</td><td class="padding-r20px"><select id="tdMonitor" name="tdMonitorType" style="width:150px"></select></td>
    						<td class="graybold11pt withinput">DESCRIPTION</td><td class="padding-r20px"><input type="text" id="tdDescr" name="tdDescription"></input></td>
    					</tr><tr>
    						<td class="graybold11pt withinput">SEVERITY LEVEL</td><td class="padding-r20px"><select id="tdSeverity" name="tdSeverityLevel" style="width:150px"></select></td>
    						<td class="graybold11pt withinput">TROUBLE ADVICE</td><td class="padding-r20px"><input type="text" id="tdAdvice" name="tdTroubleAdvice"></input></td>			
    					</tr>
    				</table>
    				<br>
    				<em id="tdUpdate" class="am_button"><a href="javascript:editingButtonClick('Update')"  class="on"><fmt:message key='aimir.button.update'/></a></em>
    				<em id="tdApply" class="am_button"><a href="javascript:editingButtonClick('Apply')"  class="on"><fmt:message key='aimir.button.apply'/></a></em>
    				<em id="tdCancel" class="am_button"><a href="javascript:editingButtonClick('Cancel')"  class="on"><fmt:message key='aimir.cancel'/></a></em>    				    				
    			</div>
    		</div>
    	</div>
    </div>
	
	<!-- INSERT START SP-193 -->
    <div id="pane-data-threshold" class="gadget_body">    	
    	<div class="flexlist margin-t5px">
    	<label class="check margin-t5px">Threshold Management</label>
		<table class="search">
			<tr>
				<td class="bold">Kind</td>
				<td class="bold">Threshold</td>
				<!-- 
				<td><span class="bold">Schedule</span><a href="javascript:makeHelpWindow();" title="How to set schedule!"><label class="icon_help"></label></a></td>
				-->
				<td></td>
			</tr>
			<tr>
				<td class="padding-r20px"><input type="text" disabled="disabled" id="kind1" name="Kind1" style="width:200px"></input></td>
				<td class="padding-r20px"><input type="text" id="threshold1" name="Threshold1"></input></td>
				<!-- 
				<td class="padding-r20px"><input type="text" id="schedule1" name="Schedule1" style="width:300px"></input></td>
				-->
			</tr>
			<tr>
				<td class="padding-r20px"><input type="text" disabled="disabled" id="kind2" name="Kind2" style="width:200px"></input></td>
				<td class="padding-r20px"><input type="text" id="threshold2" name="Threshold2"></input></td>
				<!-- 
				<td class="padding-r20px"><input type="text" id="schedule2" name="Schedule2" style="width:300px"></input></td>
				-->
			</tr>
			<tr>
				<td class="padding-r20px"><input type="text" disabled="disabled" id="kind3" name="Kind3" style="width:200px"></input></td>
				<td class="padding-r20px"><input type="text" id="threshold3" name="Threshold3"></input></td>
				<!-- 
				<td class="padding-r20px"><input type="text" id="schedule3" name="Schedule3" style="width:300px"></input></td>
				-->
			</tr>
			<tr>
				<td class="padding-r20px"><input type="text" disabled="disabled" id="kind4" name="Kind4" style="width:200px"></input></td>
				<td class="padding-r20px"><input type="text" id="threshold4" name="Threshold4"></input></td>
				<!-- 
				<td class="padding-r20px"><input type="text" id="schedule4" name="Schedule4" style="width:300px"></input></td>
				-->
			</tr>
			<tr>
				<td class="padding-r20px"><input type="text" disabled="disabled" id="kind5" name="Kind5" style="width:200px"></input></td>
				<td class="padding-r20px"><input type="text" id="threshold5" name="Threshold5"></input></td>
				<!-- 
				<td class="padding-r20px"><input type="text" id="schedule5" name="Schedule5" style="width:300px"></input></td>
				-->
			</tr>
		</table>
		<p></p>
		<em id="Save" class="am_button"><a href="javascript:saveButtonClick();"  class="on"><fmt:message key='aimir.save2'/></a></em>
		<p></p>
	 	<input type="text" id="thresholdCount" name="ThresholdCount" style="visibility:hidden;"></input>	
    </div>
    </div>
	<!-- INSERT END SP-193 -->

</body>
</html>