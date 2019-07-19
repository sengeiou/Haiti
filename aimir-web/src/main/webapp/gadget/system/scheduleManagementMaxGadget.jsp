<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE" />
    <meta http-equiv="Expires" content="0" />
    <title>스케쥴러 관리 가젯</title>
    
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/FChartStyle.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/FusionCharts.js"></script>
    
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>  

    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
        @media screen and (-webkit-min-device-pixel-ratio:0) { 
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }
        .white .x-window-body {background-color: white;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }

		html {
				overflow-y: auto;
		}
    </style>
    <script type="text/javascript">

        var supplierId;
        var loginId = "";
        var isExecute = false;
        // 수정권한
        var editAuth = "${editAuth}";

        function getJobFmtMessage() {
            var fmtMessage = new Array();

            fmtMessage[0]  = '<fmt:message key="aimir.checkSchedulerRun"/>'; //"스케쥴러 실행 여부를 확인해 주세요";
            fmtMessage[1]  = '<fmt:message key="aimir.all"/>';        //"전체";
            fmtMessage[2]  = '<fmt:message key="aimir.jobName"/>';        //"작업명";

            return fmtMessage;
        }

        function getTriggerFmtMessage() {
            var fmtMessage = new Array();

            fmtMessage[0]  = '<fmt:message key="aimir.scheduleName"/>'; //"전체";
            fmtMessage[1]  = '<fmt:message key="aimir.starttime"/>';        //"시작시각";
            fmtMessage[2]  = '<fmt:message key="aimir.prevFireTime"/>';        //"이전실행시각";
            fmtMessage[3]  = '<fmt:message key="aimir.nextFireTime"/>';        //"다음실행시각";
            fmtMessage[4]  = '<fmt:message key="aimir.state"/>';        //"상태";
            fmtMessage[5]  = '<fmt:message key="aimir.result"/>';        //"결과";
            fmtMessage[6]  = '<fmt:message key="aimir.operator"/>';        //"수행자";

            return fmtMessage;
        }

        function getLogFmtMessage() {
            var fmtMessage = new Array();

            fmtMessage[0]  = '<fmt:message key="aimir.date"/>'; //"전체";
            fmtMessage[1]  = '<fmt:message key="aimir.jobName"/>';        //"작업명";
            fmtMessage[2]  = '<fmt:message key="aimir.scheduleName"/>';        //"스케쥴명";
            fmtMessage[3]  = '<fmt:message key="aimir.result"/>';        //"결과";
            fmtMessage[4]  = '<fmt:message key="aimir.message"/>';        //"메시지";

            return fmtMessage;
        }

        function modifyStartDate(setDate, inst) {

            if (setDate) {
                setDate.replace('/','').replace('/','');


            $.ajax({url: '${ctx}/common/convertLocalDate.do',
                dataType: 'json',
                data: {dbDate:setDate, supplierId:supplierId},
                async: false,
                success: function(json) {

                    $("#startDate").val(json.localDate);
                    $("#startDate").trigger('change');
                   }
                });
            }
        }

        function modifyEndDate(setDate, inst) {

            if(setDate){
                setDate.replace('/','').replace('/','');
                $.ajax({url: '${ctx}/common/convertLocalDate.do',
                    dataType: 'json',
                    data: {dbDate:setDate, supplierId:supplierId},
                    async: false,
                    success: function(json) {

                        $("#endDate").val(json.localDate);
                        $("#endDate").trigger('change');
                    }
                });
            }
        }

        $(document).ready(function() {
            // Tooltip사용을 위해서 반드시 선언해야 한다.
            Ext.QuickTips.init();

            // 수정권한 체크
            if (editAuth == "true") {
                $("#saveBtnList").show();
                $("#playBtn").show();
            } else {
                $("#saveBtnList").hide();
                $("#playBtn").hide();
            }

            // Group Type Combo 생성
            // Group Type 은 메시지 프로퍼티에 등록되어있음
            var groupTypeProp = '<fmt:message key="aimir.task.grouptypelist"/>';
            var groupTypeList = groupTypeProp.split(",");
            var groupTypeArr = new Array();
            var groupTypeLen = groupTypeList.length;

            for (var i = 0 ; i < groupTypeLen ; i++) {
                var obj = new Object();
                obj.id = groupTypeList[i];
                obj.name = groupTypeList[i];
                groupTypeArr.push(obj);
            }

            $("#groupType").noneSelect(groupTypeArr);
            $("#groupType").selectbox();
            $("#group").selectbox();

            var sId;

            $.ajax({url: '${ctx}/common/getUserInfo.do',
                dataType: 'json',
                data: {},
                async: false,
                success: function(json) {

                    if (json.supplierId != "") {
                        sId = json.supplierId;
                        if (sId != "") {
                            supplierId= sId;
                            loginId = json.loginId;
                            $("#startDate")    .datepicker({
                                maxDate:'+0m',showOn: 'button',
                                dateFormat:'yymmdd',
                                buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
                                onSelect: function(dateText, inst) {  modifyStartDate(dateText, inst);},
                                buttonImageOnly: true});

                            $("#endDate")    .datepicker({
                                maxDate:'+0m',showOn: 'button',
                                dateFormat:'yymmdd',
                                buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
                                onSelect: function(dateText, inst) {  modifyEndDate(dateText, inst);},
                                buttonImageOnly: true});
                             var curDate = new Date();
                             $("#startDate").val($.datepicker.formatDate('yymmdd', new Date(Date.parse(curDate)-7*1000*60*60*24)));
                             $("#endDate").val($.datepicker.formatDate('yymmdd', curDate));

                             modifyStartDate($("#startDate").val(), '');
                             modifyEndDate($("#endDate").val(), '');
                        }
                    }

                    // Result Combo 생성
                    $.post("${ctx}/gadget/system/schedule/getScheduleResultComboData.do",
                            {},
                            function(json) {
                                 if (json.result != null) {
                                     $("#result").loadSelect(json.result);
                                     $("#result").selectbox();
                                 }
                            });
                }
            });
            
            $('#jobRow').val(0);
            $('#triggerRow').val(0);
            jobIntegration();

            // Group Type change
            $("#groupType").bind('change',function(event) {
                getGroupCombo($("#groupType").val());
            });

            // bind click event
            $("#add").bind('click',function(event) {readyAddJobTrigger(); });
            $("#update").bind('click',function(event) {updateTrigger(); });
            $("#delete").bind('click',function(event) {deleteTrigger(); });
            $("#pause").bind('click',function(event) {pTrigger(); });
            $("#ok").bind('click',function(event) {addJobTrigger(); });
            $("#cancel").bind('click',function(event) {cancelAddJobTrigger(); });
            $("#runBtn").bind('click',function(event) {directRunJob(); });
			
			
			
			maxInputField = 5;
			subDataFieldCount = 0;
			wrapperCount = $("#InputsWrapper").length - 1;
			$("#InputsWrapper").sortable();
			
			$("#subDataPlusButton").bind('click', function(event){
				if(wrapperCount <= maxInputField){
					subDataFieldCount++;
					$("#InputsWrapper").append(									  
						  '  <tr class="subDataFields" id="InputsWrappser_'+ subDataFieldCount +'">'
						+ '	  <td></td>'
						+ '	  <td class="padding-r20px">Text '+ subDataFieldCount +'</td>'
						+ '	  <td colspan="2">'
						+ '		<textarea type="text" id="field_'+ subDataFieldCount +'" name="subDatas" style="width:350px;"/>'
						+ '		<button class="removeClass">X</button>'
						+ '   </td>'
						+ '  </tr>'
					);  					
					wrapperCount++;
				}
				return false;													  
			});
	
			$(".removeClass").live('click', function(){
				$(this).parent().parent('tr').remove();
				wrapperCount--; 
				if (wrapperCount == 0) subDataFieldCount = 0;
				return false;													 
			});
        });

        var jobSummaryGridOn = false;
        var jobSummaryColModel;
        function jobIntegration() {
        	jobSummaryGrid();
        	jobGridFunction();
        }

		var jobSummaryGrid = function() {
            var width = $("#jobSummaryGridDiv").width();

  			var jobSummaryStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0}},
                url : '${ctx}/gadget/system/schedule/getJobDetailList.do',
                baseParams: {},
                root : 'statistic',
                listeners       : {
	                load: function(store, options){
                       	var jobData = store.reader.jsonData;
                       	if(jobData.result.length <= 0) {
                       		isExecute = false;
	                    } else {
	                    	isExecute = true;
	                    }
	                }
	            },
	            fields : [
	  	                { name: 'TOTAL', type: 'string' },
	                      { name: 'BLOCKED', type: 'string' },
	                      { name: 'COMPLETE', type: 'string' },
	                      { name: 'ERROR', type: 'string' },
	                      { name: 'NONE', type: 'string' },
	                      { name: 'NORMAL', type: 'string' },
	                      { name: 'PAUSED', type: 'string' },
	  	                ]

            });

  			jobSummaryColModel = new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    menuisabled: true,
                    align:'right',
                    width: (width-4)/7
                },
                columns: [{
                    header: "<fmt:message key='aimir.all'/>",
                    dataIndex: 'TOTAL'
                },{
                    header: "BLOCKED",
                    dataIndex: 'BLOCKED'
                },{
                    header: "COMPLETE",
                    dataIndex: 'COMPLETE'
                },{
                    header: "ERROR",
                    dataIndex: 'ERROR'
                },{
                    header: "NONE",
                    dataIndex: 'NONE'
                },{
                    header: "NORMAL",
                    dataIndex: 'NORMAL'
                },{
                    header: "PAUSED",
                    dataIndex: 'PAUSED'
                }]
            });

             if (!jobSummaryGridOn) {
                jobSummaryGrid = new Ext.grid.GridPanel({
                    layout : 'fit',
                    width : width,
                    height : 50,
                    store : jobSummaryStore,
                    colModel : jobSummaryColModel,
                    stripeRows : true,
                    columnLines : true,
                    deferRowRender : true,
                    autoScroll : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'jobSummaryGridDiv'
                });
                jobSummanryGridOn = true;
            }
		}
		
        var jobGridOn = false;
        var jobGridColModel;
        var jobGrid;
        var jobGridFunction = function() {
            var width = $("#jobGridDiv").width();
            var jobGridStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0}},
                url : '${ctx}/gadget/system/schedule/getJobDetailList.do',
                baseParams: {},
                root : 'result',
                listeners       : {
                    load: function(store, options){
                    	var jobData = store.reader.jsonData;
                    	if(jobData.result.length > 0) {
                           	var selectJob = store.reader.jsonData.result[$('#jobRow').val()];
                           	var resultLength = store.reader.jsonData.result.length;
                           	getTriggerDetail(selectJob.name,selectJob.className,selectJob.description,$('#jobRow').val(),0,resultLength,selectJob.group);
                    	} else {
                    		getTriggerDetail('','','',0,0,0,'');
                    	}
                    }
                },
                fields : [
                          { name: 'name', type: 'string' },
                         { name: 'BLOCKED', type: 'string' },
                         { name: 'COMPLETE', type: 'string' },
                         { name: 'ERROR', type: 'string' },
                         { name: 'NONE', type: 'string' },
                         { name: 'NORMAL', type: 'string' },
                         { name: 'PAUSED', type: 'string' }
                         ] 
            });

            jobGridColModel = new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    menuDisabled: true,
                    width: (width-4)/7
                },
                columns: [{
                    header: "<fmt:message key='aimir.jobName'/>",
                    dataIndex: 'name',
                    align:'left'
                },{
                    header: "BLOCKED",
                    dataIndex: 'BLOCKED',
                    align:'right'
                },{
                    header: "COMPLETE",
                    dataIndex: 'COMPLETE',
                    align:'right'
                },{
                    header: "ERROR",
                    dataIndex: 'ERROR',
                    align:'right'
                },{
                    header: "NONE",
                    dataIndex: 'NONE',
                    align:'right'
                },{
                    header: "NORMAL",
                    dataIndex: 'NORMAL',
                    align:'right'
                },{
                    header: "PAUSED",
                    dataIndex: 'PAUSED',
                    align:'right'
                }]
            });

             if (!jobGridOn) {
                jobGrid = new Ext.grid.GridPanel({
                    layout : 'fit',
                    width : width,
                    height : 670,
                    store : jobGridStore,
                    colModel : jobGridColModel,
                     selModel : new Ext.grid.RowSelectionModel({
                        singleSelect : true,
                        listeners : {
                            rowselect : function(selectionModel, columnIndex, value) {
                            	var job = value.json;
                            	$('#triggerRow').val(0);
                            	getTriggerDetail(job.name,job.className,job.description,columnIndex,0,value.store.totalLength,job.group);
                            }
                        }
                    }), 
                    stripeRows : true,
                    columnLines : true,
                    deferRowRender : true,
                    autoScroll : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'jobGridDiv',
                    viewConfig : {
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    }
                });
                jobGridOn = true;
            }  else {
                jobGrid.setWidth(width);
                jobGridStore.reload();
                jobGrid.reconfigure(jobGridStore, jobGridColModel);
            }
        };

        var triggerGridOn = false;
        var triggerGridStore;
        var triggerGridColModel;
        var triggerGrid;
        function triggerGridFunction() {
            var width = $("#triggerGridDiv").width();

            triggerGridStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0}},
                url : '${ctx}/gadget/system/schedule/getTriggerOfJob.do',
                baseParams: {
                    jobName : $('#jobName').val()
                },
                root : 'result',
                totalProperty : 'totalCnt',
                idProperty : 'no',
                listeners       : {
                    load: function(store, options){
                    	var triggerData = store.reader.jsonData;
                    	if(triggerData.result.length > 0) {
	                    	var firstTrigger = store.reader.jsonData.result[$('#triggerRow').val()];
	                    	setTriggerInfo(firstTrigger.name,firstTrigger.cron,firstTrigger.repeatInterval,firstTrigger.cronExpression,$('#triggerRow').val()
	                    			,firstTrigger.status,firstTrigger.repeatCount,firstTrigger.simpleStartTime,firstTrigger.simpleEndTime);
		                	setTriggerSelect(store.reader.jsonData.result,store.reader.jsonData.result.length);
                    	} else {
                    		setTriggerSelect('',0);
                    	}
                    	
                    }
                },
                fields : [
                    { name: 'name', type: 'string' },
                    { name: 'startTime', type: 'string' },
                    { name: 'previousFireTime', type: 'string' },
                    { name: 'nextFireTime', type: 'string' },
                    { name: 'status', type: 'string' },
                    { name: 'result', type: 'string' },
                    { name: 'operator', type: 'string' },
                    { name: 'cron', type: 'string' },
                    { name: 'cronExpression', type: 'string' },
                    { name: 'simpleStartTime', type: 'auto' },
                    { name: 'simpleEndTime', type: 'auto' },
                    { name: 'repeatCount', type: 'string' },
                    { name: 'repeatInterval', type: 'string' },
                ]
            });
            triggerGridColModel = new Ext.grid.ColumnModel({
                defaults: {
                    sortable: true,
                    menuDisabled: true,
                    width: (width-4)/7
                },
                columns: [{
                    header: "<fmt:message key='aimir.scheduleName'/>",
                    dataIndex: 'name',
                    align:'center',
                    sortable: true,
                    renderer : addTooltip
                },{
                    header: "<fmt:message key='aimir.starttime'/>",
                    dataIndex: 'startTime',
                    align:'center',
                    sortable: true,
                    renderer : addTooltip
                },{
                    header: "<fmt:message key='aimir.prevFireTime'/>",
                    dataIndex: 'previousFireTime',
                    align:'center',
                    sortable: true,
                    renderer : addTooltip
                },{
                    header: "<fmt:message key='aimir.nextFireTime'/>",
                    dataIndex: 'nextFireTime',
                    align:'center',
                    sortable: true,
                    renderer : addTooltip
                },{
                    header: "<fmt:message key='aimir.state'/>",
                    dataIndex: 'status',
                    align:'center',
                    sortable: true,
                    renderer : renderTriggerState
                },{
                    header: "<fmt:message key='aimir.result'/>",
                    dataIndex: 'result',
                    align:'center',
                    sortable: true,
                    renderer : addTooltip
                },{
                    header: "<fmt:message key='aimir.operator'/>",
                    dataIndex: 'operator',
                    align:'center',
                    sortable: true,
                    renderer : addTooltip
                }]
            });

            if (!triggerGridOn) {
                triggerGrid = new Ext.grid.GridPanel({
                    layout : 'fit',
                    width : width,
                    height : 140,
                    store : triggerGridStore,
                    colModel : triggerGridColModel,
                     selModel : new Ext.grid.RowSelectionModel({
                        singleSelect : true,
                        listeners : {
                            rowselect : function(selectionModel, columnIndex, value) {
                            	var trigger = value.json;
                            	setTriggerInfo(trigger.name,trigger.cron,trigger.repeatInterval,trigger.cronExpression,columnIndex,
                            			trigger.status,trigger.repeatCount,trigger.simpleStartTime,trigger.simpleEndTime);
                            }
                        }
                    }), 
                    stripeRows : true,
                    columnLines : true,
                    deferRowRender : true,
                    autoScroll : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'triggerGridDiv',
                    viewConfig : {
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    }
                });
                triggerGridOn = true;
            } else {
                triggerGrid.setWidth(width);
                triggerGridStore.reload();
                triggerGrid.reconfigure(triggerGridStore, triggerGridColModel);
            }
        };
        
        function renderTriggerState(value, metadata) {
            switch(value) {
        	case '0' :
        		value = 'NORMAL';
        		break;
        	case '1' :
        		value = 'PAUSED';
        		break;
        	case '2' :
        		value = 'COMPLETE';
        		break;
        	case '3' :
        		value = 'ERROR';
        		break;
        	case '4' :
        		value = 'BLOCKED';
        		break;
        	default :
        		value = 'NONE';
        		break;
        	}
            
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            
            return value;
        }

		var maxInputField;
		var subDataFieldCount;
		var wrapperCount;
		
		function addSubDataFunc(index, text){
			if(wrapperCount <= maxInputField){
				subDataFieldCount++;
				$("#InputsWrapper").append(									  
					  '  <tr class="subDataFields" id="InputsWrappser_'+ index +'">'
					+ '	  <td></td>'
					+ '	  <td class="padding-r20px">Text '+ index +'</td>'
					+ '	  <td colspan="2">'
					+ '		<textarea type="text" id="field_'+ index +'" name="subDatas" style="width:350px;">' + text + '</textarea>'
					+ '		<button class="removeClass">X</button>'
					+ '   </td>'
					+ '  </tr>'
				);  					
				wrapperCount++;
			}
        }

		function deleteSubDataFunc(){
			$(".subDataFields").remove();
			wrapperCount = 0;
			subDataFieldCount = 0;
		}		
	
		
		
        var selectedJobName = null;
        function getTriggerDetail(jobName,jobClassName,jobDescription,jobRow,triggerRow,jobMaxRow,group) {
            $("#jobName").val(jobName);
            selectedJobName = jobName;
            $("#jobClassName").val(jobClassName);
            $("#jobDescription").val(jobDescription);
            $("#jobRow").val(jobRow);
        
            if (jobMaxRow != null) {
                $("#jobMaxRow").val(jobMaxRow);
            }

            if (group != null) {
                getGroupType(group);
            }
           
            buttonControl('');
            triggerGridFunction();
           
        }

  
        /*function enableJobInfo(b) {
            if (b) {
                $("#jobName").show();
                $("#jobNameView").hide();
                $("#jobClassName").show();
                $("#jobClassNameView").hide();
            } else {
                $("#jobName").hide();
                $("#jobNameView").show();
                $("#jobClassName").hide();
                $("#jobClassNameView").show();
            }
        }*/

        function getGroupCombo(groupType, group) {
            $.post("${ctx}/gadget/system/schedule/getGroupComboDataByGroupType.do",
                    {groupType : groupType},
                    function(json) {
                         if (json.result != null) {
                             $("#group").pureSelect(json.result);

                             if (group != null) {
                                 $("#group").val(group);
                             }
                             $("#group").selectbox();
                         }
                    });
        }

        function getGroupType(group) {
            if (group == null || group == "") {
                //$("#groupType option:first").attr("selected", "selected");
                $("#groupType").val("");
                $("#groupType").selectbox();
                getGroupCombo("");
                return;
            }
            $.post("${ctx}/gadget/system/schedule/getGroupTypeByGroup.do",
                    {group : group},
                    function(json) {
                         if (json.result != null) {
                             $("#groupType").val(json.result);
                             $("#groupType").selectbox();
                             getGroupCombo(json.result, group);
                         }
                    });
        }

        function setTriggerSelect(triggerNameArr,maxRow) {
            $("#triggerMaxRow").val(maxRow);
            $('#triggerNameSelect').loadSelect(triggerNameArr);
            $('#triggerNameSelect').selectbox();
            getScheduleResultLogByJobName();
        }

        /* Scheduler Log 리스트 START */
        var logGridOn = false;
        var logGrid;
        var logGridColModel;
        var errorMsg = null;
        var logGridStore;
        var getScheduleResultLogByJobName = function() {
            var width = $("#logGridDiv").width();
            var pageSize = 5;

            logGridStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/system/schedule/getScheduleResultLogByJobName.do",
                baseParams : {
                    supplierId : supplierId,
                    startDate : $("#startDate").val(),
                    endDate : $("#endDate").val(),
                    jobName : $("#jobName").val(),
                    triggerName : ($("#triggerNameSelect").val() == null) ? "" : $("#triggerNameSelect").val(),
                    result : ($("#result").val() == null) ? "" : $("#result").val()
                },
                totalProperty : 'total',
                root : 'logList',
                fields : ["createTime", "jobName", "triggerName", "result", "errorMessage"],
                listeners : {
                    beforeload: function(store, options){
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    },
                	load: function() {
                		//스케줄러가 현재 실행중인지 확인 후 경고창을 보임
                		 if(!isExecute) {
    						Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.checkSchedulerRun'/>");
    					}
                	}
                }
            });

            logGridColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "Date", dataIndex: 'createTime', width: width/5-7,  renderer: addTooltip}
                   ,{header: "Job Name", dataIndex: 'jobName', width: width/5, renderer: addTooltip}
                   ,{header: "Schedule Name", dataIndex: 'triggerName', width: width/5, align: "left", renderer: addTooltip}
                   ,{header: "Result", dataIndex: 'result', width: width/5-30, align: "left", renderer: addTooltip}
                   ,{header: "Message", dataIndex: 'errorMessage', width: width/5+30, align: "left", renderer: addTooltip
                      /* renderer: function(value, metadata) {
                       var hasFail = value.match(/^fail/);
                       if(hasFail != null) {
                           errorMsg = value.split("[");
                           if(errorMsg != null) {             
                               var tpl = new Ext.Template("<a href='javascript:showErrorDetails();' title='Please click this cell if you want to re-run a scheduler with failed meter.'>"+value+"</a>");
                               return tpl.apply({errorMessage: value});
                           }else {
                               if (value != null && value != "") {
                                   metadata.attr = 'ext:qtip="' + value + '"';
                               }
                               return value;
                           }
                       }else {
                           if (value != null && value != "") {
                               metadata.attr = 'ext:qtip="' + value + '"';
                           }
                           return value;
                       }
                     } */

                   }                   
                ],
                defaults: {
                    sortable: false
                   ,menuDisabled: true
                   ,width: 30
               }
            });

            if(logGridOn == false) {
                logGrid = new Ext.grid.GridPanel({
                    store: logGridStore,
                    colModel : logGridColModel,
                   // sm: new Ext.grid.RowSelectionModel({ singleSelect:true }),
                    autoScroll:false,
                    width: width,
                    height: 173,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'logGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: logGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                logGridOn = true;
            } else {
                logGrid.setWidth(width);
                var bottomToolbar = logGrid.getBottomToolbar();
                logGrid.reconfigure(logGridStore, logGridColModel);
                bottomToolbar.bindStore(logGridStore);
            }
           // hide();
        };

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        var errorMeterList = new Array();
        function showErrorDetails() {

            var temp1 = (errorMsg.length == 2 ? errorMsg[1].split("]") : null);
            if(temp1[0] != null) {
                errorMeterList = temp1[0].split(",");
            }
            makeErrorMeterListWindow();
        }

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
                    title : 'How to set cron expression ',
                    id : 'helpWinId',
                    applyTo : 'helpDiv',
                    modal: false,
                    autoScroll : true,
                    //autoHeight : true,
                    pageX : 400,
                    pageY : 10,
                    height: 500,
                    width: winW,
                    html: "<span style='line-height:20px'>" + html + "</span>",
                    closeAction : 'hide',
                    constrain:true,
                    cls:'white'
                });
            }
            //imgWin.show(this);

            Ext.getCmp('helpWinId').show();
        }
 
        var array = new Array;
        var imgWin;
        var gridOn = false;
        var grid;
        var store;
        var gridH = 300;
        var winH = 300;
        var meterCheckSelModel;
        var colModel;
        //팝업 윈도우 생성
        function makeErrorMeterListWindow() {
            store = new Ext.data.SimpleStore({fields:['key','mdsId'],data:[]});
            var myRecord = Ext.data.Record.create([{name:'key'},{name:'mdsId'}]);
            var rec = new Array();
            for(var i=0; i<errorMeterList.length; i++) {
                rec[i] = new myRecord({key:i+1, mdsId:errorMeterList[i]});
                store.insert(i, rec);
            }

            if (gridOn == false) {
                meterCheckSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly:true
                    ,dataIndex: 'mdsId'
                });
            }
            if (gridOn == false) {
                colModel = new Ext.grid.ColumnModel({
                    defaults : {
                        width : 100,
                        height : 170,
                        sortable : true
                    },
                    columns : [
                    meterCheckSelModel,
                    {
                        id : "mdsId",
                        width : 170,
                        header : "Meter ID",
                        dataIndex : "mdsId"
                    }]
    
                });
            }
            //그리드 설정
            if (gridOn == false) {
                grid = new Ext.grid.GridPanel({
                    height : gridH,
                    store : store,
                    colModel : colModel,
                    sm: meterCheckSelModel,
                    width : 200,
                    tbar:[{
                        //text : "Run",
                        iconCls:'btn_play',
                        handler: function() {
                           runSchedule();
                        }
                     },'-']
                });

                gridOn = true;
            } else {
                grid.reconfigure(store, colModel);
            }

            //윈도우 설정    
            if (!imgWin) {
                imgWin = new Ext.Window({
                    title : 'Error Meter List',
                    id : 'errorMeterListWinId',
                    applyTo : 'errorMeterListDiv',
                    autoScroll : true,
                    autoHeight : true,
                    pageX : 400,
                    pageY : 130,
                    width : 214,
                    height : winH,
                    items : grid,
                    closeAction : 'hide'
                });
            } else {
                
            }
            //imgWin.show(this);
            Ext.getCmp('errorMeterListWinId').show();
        }

        function runSchedule() {
             var checkedArr = meterCheckSelModel.getSelections();
             var len = checkedArr.length;
             Ext.Msg.alert('<fmt:message key='aimir.message'/>',"You can check the result of scheduler in the scheduler log grid after a few minutes.");

        }

        function setTriggerInfo(triggerName,cron,repeatInterval,cronExpression,row,state,repeatCount,simpleStartTime,simpleEndTime) {
            $.ajaxSetup({ async: false });

            initTriggerinfo();

            $("#triggerRow").val(row);
            $("#cron").val(cron);
            $("#triggerName").val(triggerName);

            if (!cron || cron == 'false') {
                $(":input:radio").filter("input[value='S']").attr("checked", "checked");

                if (repeatInterval) {
                    $("#repeatInterval").val(repeatInterval);
                    $("#cronExpression").val("");
                }

                //repeatcount는 반복실행횟수로 이 값이 0이면 한번만 실행되고 1이면 총 2번 시실행된다.(-1 또는 빈값은 무한반복)
                if(repeatCount == -1) {
                    $("#repeatCount").val("");
                } else {
                    $("#repeatCount").val(repeatCount);
                }

                if(simpleStartTime != null && simpleStartTime[0] != 0) {
                    $("#simpleStartTime_min").val(simpleStartTime[4]);
                    $("#simpleStartTime_hou").val(simpleStartTime[3]);
                    $("#simpleStartTime_day").val(simpleStartTime[2]);
                    $("#simpleStartTime_mon").val(simpleStartTime[1]);
                    $("#simpleStartTime_year").val(simpleStartTime[0]);
                }

                if(simpleEndTime != null && simpleEndTime[0] != 0) {
                    $("#simpleEndTime_min").val(simpleEndTime[4]);
                    $("#simpleEndTime_hou").val(simpleEndTime[3]);
                    $("#simpleEndTime_day").val(simpleEndTime[2]);
                    $("#simpleEndTime_mon").val(simpleEndTime[1]);
                    $("#simpleEndTime_year").val(simpleEndTime[0]);
                }
            } else {

                $(":input:radio").filter("input[value='C']").attr("checked", "checked");

                if (cronExpression) {
                    $("#repeatInterval").val("");
                    $("#cronExpression").val(cronExpression);

                    var cronExpressionList = cronExpression.split(" ");
                    if(cronExpressionList.length == 6) {
                        $("#cronExpression_sec").val(cronExpressionList[0]);
                        $("#cronExpression_min").val(cronExpressionList[1]);
                        $("#cronExpression_hou").val(cronExpressionList[2]);
                        $("#cronExpression_day").val(cronExpressionList[3]);
                        $("#cronExpression_mon").val(cronExpressionList[4]);
                        $("#cronExpression_dayOfWeek").val(cronExpressionList[5]);
                    }
                }
            }

            $("#pauseStatus").val(state);
            if (state == '0') {
                $("#pause").html('<fmt:message key="aimir.stop"/>');
            } else if (state=='1') {
                $("#pause").html('<fmt:message key="aimir.start"/>');
            }
 
            var groupId;
            var groupType;
            if(triggerName != null) {
            $.post('${ctx}/gadget/system/schedule/getTriggerDataMap.do'
            		, { jobName : triggerName, loginId : loginId}
            		, function( json ) {     
                        groupId = json.groupId;
                        groupType = json.groupType;

						deleteSubDataFunc();						
                        if(typeof(json.subJobData) != 'undefined') {
							$.each(json.subJobData, function(name, value){								
								addSubDataFunc(name, decodeURIComponent(value));
							});							
						}
                      }
            );
            }
            // Group Type Combo 생성
            // Group Type 은 메시지 프로퍼티에 등록되어있음
            var groupTypeProp = '<fmt:message key="aimir.task.grouptypelist"/>';
            var groupTypeList = groupTypeProp.split(",");
            var groupTypeArr = new Array();
            var groupTypeLen = groupTypeList.length;

            for (var i = 0 ; i < groupTypeLen ; i++) {
                var obj = new Object();
                obj.id = groupTypeList[i];
                obj.name = groupTypeList[i];
                groupTypeArr.push(obj);
            }

            $("#groupType").noneSelect(groupTypeArr);
            if(groupType != null && groupType.length !=0 ) {
                $("#groupType option[value=" + groupType + "]").attr("selected", "true");
            }
            $('#groupType').selectbox();

            getGroupCombo($("#groupType").val());
            $("#group option[value=" + groupId + "]").attr("selected", "true");
            $('#group').selectbox();
  
            $.ajaxSetup({ async: true });
        }
        
        function initTriggerinfo() {

            $("#triggerName").val("");

	        $("#simpleStartTime_min").val("");
            $("#simpleStartTime_hou").val("");
            $("#simpleStartTime_day").val("");
            $("#simpleStartTime_mon").val("");
            $("#simpleStartTime_year").val("");

            $("#simpleEndTime_min").val("");
            $("#simpleEndTime_hou").val("");
            $("#simpleEndTime_day").val("");
            $("#simpleEndTime_mon").val("");
            $("#simpleEndTime_year").val("");

            $("#repeatCount").val("");            
            $("#repeatInterval").val("");            

            $("#cronExpression_sec").val("");
            $("#cronExpression_min").val("");
            $("#cronExpression_hou").val("");
            $("#cronExpression_day").val("");
            $("#cronExpression_mon").val("");
            $("#cronExpression_dayOfWeek").val("");
            
            $(":input:radio").filter("input[value='S']").attr("checked", "checked");
        }

        function getJobLoading() {
            jobGridFunction();
        }

        function checkRunMessage() {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.checkSchedulerRun"/>');
        }

        function deleteTrigger() {
            var triggerName = $("#triggerName").val();
            $.post('${ctx}/gadget/system/schedule/deleteTrigger.do' , {triggerName:triggerName
                } ,
                function( json ) {
                    var jsonData = json.result;

                    if (jsonData == true) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save"/>');
                        $('#triggerRow').val(0);
                        getJobLoading();
                    } else {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save.error"/>');
                    }
                });
        }

        function pTrigger() {
            var triggerName = $("#triggerName").val();
            var pauseStatus = $("#pauseStatus").val();
            var url ='';
            if (pauseStatus==0) {
                url ='${ctx}/gadget/system/schedule/pauseTrigger.do';
            } else {
                url ='${ctx}/gadget/system/schedule/resumTrigger.do';
            }
            $.getJSON(url , {triggerName:triggerName,
                loginId:loginId
            } ,
            function(json) {
                var jsonData = json.result;
                if (jsonData == true) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save"/>');
                    getJobLoading();
                } else {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save.error"/>');
                }
            });
        }

        function updateTrigger() {

            var cronExpression = $("#cronExpression_sec").val().trim() + " " 
            + $("#cronExpression_min").val().trim() + " " 
            + $("#cronExpression_hou").val().trim() + " " 
            + $("#cronExpression_day").val().trim() + " " 
            + $("#cronExpression_mon").val().trim() + " "
            + $("#cronExpression_dayOfWeek").val().trim();

            $("#cronExpression").val(cronExpression);

            if (!validation()) {
                return;
            }

            var jobGroup = ($("#group").val() != null) ? $("#group").val() : "";			
            var groupType = ($("#groupType").val() != null) ? $("#groupType").val() : "";
            var triggerName = $("#triggerName").val();

            var repeatInterval =$("#repeatInterval").val();
            var repeatCount =$("#repeatCount").val();
            
            var startTimeArr = new Array();
            startTimeArr[0] = $('#simpleStartTime_year').val();
            startTimeArr[1] = $('#simpleStartTime_mon').val();
            startTimeArr[2] = $('#simpleStartTime_day').val();
            startTimeArr[3] = $('#simpleStartTime_hou').val();
            startTimeArr[4] = $('#simpleStartTime_min').val();

            var endTimeArr = new Array();
            endTimeArr[0] = $('#simpleEndTime_year').val();
            endTimeArr[1] = $('#simpleEndTime_mon').val();
            endTimeArr[2] = $('#simpleEndTime_day').val();
            endTimeArr[3] = $('#simpleEndTime_hou').val();
            endTimeArr[4] = $('#simpleEndTime_min').val();

            var bCron = false;

            var exp = $(":input:radio[name='exp']:checked").val();

            if (exp !='S') {
                bCron =true;
            }

            $("#cron").val(bCron);

            $.post('${ctx}/gadget/system/schedule/updateTrigger.do' , {
					triggerName : triggerName,
					expression : cronExpression,
					repeatInterval : repeatInterval,
					cron : bCron,
					groupType : groupType,
					jobGroup : jobGroup,
					startTimeArr : startTimeArr,
                	endTimeArr : endTimeArr,
                	repeatCount : repeatCount,
					loginId : loginId,
					subJobData: getPostData()
				},
                function(json) {
                    var jsonData = json.result;
                    if (jsonData == true) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save"/>');
                    } else {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save.error"/>');
                    }
                    getJobLoading();
                });
        }
        
        /* function getTriggerOfJob() {

            $.getJSON('${ctx}/gadget/system/schedule/getTriggerOfJob.do', {
                'jobName' : $('#jobName').val()
            }, function(returnData) {

                var triggerInfo = returnData.result;
                var selectTrigger = "";
                for (var i = 0; i < triggerInfo.length; i++) {
                    if(triggerInfo[i].name == $('#triggerName').val()) {
                        selectTrigger = triggerInfo[i];
                    }
                };

                setTriggerInfo(selectTrigger.name,selectTrigger.cron,selectTrigger.repeatInterval,selectTrigger.cronExpression,$("#triggerRow").val(),selectTrigger.status,selectTrigger.repeatCount,selectTrigger.simpleStartTime,selectTrigger.simpleEndTime);

            });
        } */

        // function addTrigger() {
        //     var bCron = false;
        //     if ($("#cron").val() != '') {
        //         $("#triggerName").val('');
        //         $("#cronExpression").val('');
        //         $("#repeatInterval").val('');
        //         $("#cron").val('');
        //         $("#add").val('<fmt:message key="aimir.button.register"/>');
        //         $(":input:radio").filter("input[value='S']").attr("checked", "checked");
        //     } else {
        //         if (!validation()) {
        //           return;
        //         }
        //         var exp = $(":input:radio[name='exp']:checked").val();
        // 
        //         if (exp != 'S') {
        //             bCron =true;
        //         }
        //         $("#cron").val(bCron);
        // 
        //         var jobName = $("#jobName").val();
        //         var triggerName = $("#triggerName").val();
        //         var cronExpression = $("#cronExpression").val();
        //         var repeatInterval = $("#repeatInterval").val();
        // 
        //         if (repeatInterval == '') {
        //             repeatInterval=0;
        //         }
        //         $.getJSON('${ctx}/gadget/system/schedule/addTrigger.do' , {
        //             jobName:jobName,
        //             triggerName:triggerName,
        //             expression:cronExpression,
        //             repeatInterval:repeatInterval,
        //             cron:bCron,
        //             loginId:loginId
        //             } ,
        //             function(json) {
        //                 var jsonData = json.result;
        //                 alert(jsonData);
        //                 getTriggerDetail($("#jobName").val(),$("#jobClassName").val(),$("#jobDescription").val(),$("#jobRow").val(),$("#triggerMaxRow").val());
        //             });
        //     }
        // }

		//동적 생성된 파라미터 값 리턴
		function getPostData(){			
			var tempC = 0;
			var postData = "[";
			
			$('textarea[name=subDatas]').each(function(index, item){
				tempC++;
				//postData += "{\"" + tempC + "\":\"" + $(this).val().replace("\n", "\\n") + "\"},";
				postData += "{\"" + tempC + "\":\"" + encodeURIComponent($(this).val()) + "\"},";
				
			});
			
			postData = postData.substring(0, postData.length-1);			
			postData += "]";
			
			if (tempC == 0)	{
				postData = '';
			}  
			
			return postData;
		}
		
		
        function addJobTrigger() {   
            var bCron = false;
            
            var jobName = $("#jobName").val();
            var jobClassName = $("#jobClassName").val();
            var jobDescription = $("#jobDescription").val();
            var jobGroup = ($("#group").val() != null) ? $("#group").val() : "";
            var groupType = ($("#groupType").val() != null) ? $("#groupType").val() : "";
            var triggerName = $("#triggerName").val();

            var cronExpression = $("#cronExpression_sec").val().trim() + " " 
                                  + $("#cronExpression_min").val().trim() + " " 
                                  + $("#cronExpression_hou").val().trim() + " " 
                                  + $("#cronExpression_day").val().trim() + " " 
                                  + $("#cronExpression_mon").val().trim() + " "
                                  + $("#cronExpression_dayOfWeek").val().trim();

            $("#cronExpression").val(cronExpression);

            var repeatInterval = $("#repeatInterval").val();

            if (!validation()) {
              return;
            }
            var exp = $(":input:radio[name='exp']:checked").val();

            if (exp != 'S') {
                bCron =true;
            }
            $("#cron").val(bCron);

            if (repeatInterval == '') {
                repeatInterval=0;
            }
            
            var startTimeArr = new Array();
            startTimeArr[0] = $('#simpleStartTime_year').val();
            startTimeArr[1] = $('#simpleStartTime_mon').val();
            startTimeArr[2] = $('#simpleStartTime_day').val();
            startTimeArr[3] = $('#simpleStartTime_hou').val();
            startTimeArr[4] = $('#simpleStartTime_min').val();

            var endTimeArr = new Array();
            endTimeArr[0] = $('#simpleEndTime_year').val();
            endTimeArr[1] = $('#simpleEndTime_mon').val();
            endTimeArr[2] = $('#simpleEndTime_day').val();
            endTimeArr[3] = $('#simpleEndTime_hou').val();
            endTimeArr[4] = $('#simpleEndTime_min').val();

            var repeatCnt = $('#repeatCount').val();

            $.post('${ctx}/gadget/system/schedule/addJobTrigger.do', {
                jobName : jobName,
                jobClassName : jobClassName,
                jobDescription : jobDescription,
                groupType : groupType,
                jobGroup : jobGroup,
                triggerName : triggerName,
                expression : cronExpression,
                repeatInterval : repeatInterval,
                startTimeArr : startTimeArr,
                endTimeArr : endTimeArr,
                cron : bCron,
                repeatCount : repeatCnt,
                loginId : loginId,
				subJobData: getPostData()
                },
                function(json) {
                    buttonControl('');
                    var jsonData = json.result;
                    if (jsonData == true) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save"/>');
                        deleteSubDataFunc();

                        if (selectedJobName == $("#jobName").val()) {     // add trigger
                            getTriggerDetail($("#jobName").val(),$("#jobClassName").val(),$("#jobDescription").val(),$("#jobRow").val(),$("#triggerMaxRow").val());
                        } else {        // add job
                            getJobLoading();
                        }
                    } else {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save.error"/>');
                    }
                    if (selectedJobName == $("#jobName").val()) {     // add trigger
                        getTriggerDetail($("#jobName").val(),$("#jobClassName").val(),$("#jobDescription").val(),$("#jobRow").val(),$("#triggerMaxRow").val());
                    } else {        // add job
                        getJobLoading();
                    }
                });   
        }

        function validation() {

            if ($("#jobName").val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.jobNameNotExist"/>');
                return false;
            }

            if ($("#jobClassName").val() == "") {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.jobClassNameNotExist"/>');
                return false;
            }

            var exp = $(":input:radio[name='exp']:checked").val();
            var triggerName = $("#triggerName").val();
            if (triggerName == '') {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.scheduleNameNotExist"/>');
               return false;
            }
            if (exp == 'C') {
                var cronExpression = $("#cronExpression").val();
                var cronExpression_sec = $("#cronExpression_sec").val().trim();
                var cronExpression_min = $("#cronExpression_min").val().trim();
                var cronExpression_hou = $("#cronExpression_hou").val().trim();
                var cronExpression_day = $("#cronExpression_day").val().trim();
                var cronExpression_mon = $("#cronExpression_mon").val().trim();
                var cronExpression_dayOfWeek = $("#cronExpression_dayOfWeek").val().trim();

                if (cronExpression_sec == '' || cronExpression_min == '' || 
                        cronExpression_hou == '' || cronExpression_day == '' ||
                        cronExpression_mon == '' || cronExpression_dayOfWeek == '' ) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.cronExpNotExist"/>');
                   return false;
                }
                var valid= false;
                $.ajax({url: '${ctx}/gadget/system/schedule/validCronExpression.do',
                    dataType: 'json',
                    data: {cronExpression:cronExpression},
                    async: false,
                    success: function(json) {

                        valid=json.result;
                    }
                });

                if (!valid) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.cronExpNotValid"/>');
                    return false;
                }
            }

            if (exp == 'S') {
                var startTimeCnt = 0;
                var endTimeCnt = 0;
                
                var startTime_year = $("#simpleStartTime_year").val();
                var startTime_mon = $("#simpleStartTime_mon").val();
                var startTime_day = $("#simpleStartTime_day").val();
                var startTime_hou = $("#simpleStartTime_hou").val();
                var startTime_min = $("#simpleStartTime_min").val();
                
                
                var endTime_year = $("#simpleEndTime_year").val();
                var endTime_mon = $("#simpleEndTime_mon").val();
                var endTime_day = $("#simpleEndTime_day").val();
                var endTime_hou = $("#simpleEndTime_hou").val();
                var endTime_min = $("#simpleEndTime_min").val();

                if(startTime_year != "" && (isNaN(startTime_year) || startTime_year.length < 4) ){
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startTime.validation'/>");
                    return false;
                }
                startTimeCnt += validationDateCnt(startTime_year);

                if(startTime_mon != "" && (isNaN(startTime_mon) || startTime_mon.length != 2 || startTime_mon > 12 || startTime_mon < 1 )) {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startTime.validation'/>");
                    return false;
                }
                startTimeCnt += validationDateCnt(startTime_mon);

                if(startTime_day != "" && (isNaN(startTime_day) || startTime_day.length != 2 || startTime_day > 31 || startTime_day < 1 )) {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startTime.validation'/>");
                    return false;
                }
                startTimeCnt += validationDateCnt(startTime_day);
                if(startTime_hou != "" && (isNaN(startTime_hou) || startTime_hou.length != 2 || startTime_hou > 23 || startTime_hou < 0 )) {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startTime.validation'/>");
                    return false;
                }
                startTimeCnt += validationDateCnt(startTime_hou);
                if(startTime_min != "" && (isNaN(startTime_min) || startTime_min.length != 2 || startTime_min > 59 || startTime_min < 0 )) {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startTime.validation'/>");
                    return false;
                }
                startTimeCnt += validationDateCnt(startTime_min);

                if(startTimeCnt != 0 && startTimeCnt != 5) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startTime.validation'/>");
                    return false;
                }

                if(endTime_year != "" && (isNaN(endTime_year) && endTime_year == "" || endTime_year.length < 4)) {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.endTime.validation'/>");
                    return false;
                }
                endTimeCnt += validationDateCnt(endTime_year);
                if(endTime_mon != "" && (isNaN(endTime_mon) || endTime_mon.length != 2 || endTime_mon > 12 || endTime_mon < 1 )) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.endTime.validation'/>");
                    return false;
                }
                endTimeCnt += validationDateCnt(endTime_mon);
                if(endTime_day != "" && (isNaN(endTime_day) || endTime_day.length != 2 || endTime_day > 31 || endTime_day < 1 )) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.endTime.validation'/>");
                    return false;
                }
                endTimeCnt += validationDateCnt(endTime_day);
                if(endTime_hou != "" && (isNaN(endTime_hou) || endTime_hou.length != 2 || endTime_hou > 23 || endTime_hou < 0 )) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.endTime.validation'/>");
                    return false;
                }
                endTimeCnt += validationDateCnt(endTime_hou);
                if(endTime_min != "" && (isNaN(endTime_min) || endTime_min.length != 2 || endTime_min > 59 || endTime_min < 0 )) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.endTime.validation'/>");
                    return false;
                }
                endTimeCnt += validationDateCnt(endTime_min);

                if(endTimeCnt != 0 && endTimeCnt != 5) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.endTime.validation'/>");
                    return false;
                }

                if(startTime_year > endTime_year) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startEndTime.check'/>");
                    return false;
                }
                if(startTime_year == endTime_year && startTime_mon > endTime_mon) {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startEndTime.check'/>");
                    return false;    
                }
                if(startTime_year == endTime_year && startTime_mon == endTime_mon 
                    && startTime_day > endTime_day) {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startEndTime.check'/>");
                    return false;    
                }
                if(startTime_year == endTime_year && startTime_mon == endTime_mon 
                    && startTime_day == endTime_day && startTime_hou > endTime_hou) {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startEndTime.check'/>");
                    return false;    
                }
                 if(startTime_year == endTime_year && startTime_mon == endTime_mon 
                    && startTime_day == endTime_day && startTime_hou == endTime_hou &&startTime_min > endTime_min) {
                	 Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.task.startEndTime.check'/>");
                    return false;    
                }

                var startDate = new Date(startTime_year,startTime_mon-1,startTime_day,startTime_hou,startTime_min);
                var endDate = new Date(endTime_year,endTime_mon-1,endTime_day,endTime_hou,endTime_min);

                var currDbDate = $.format.date(new Date(), "yyyyMMddHHmm");
				var startDbDate = $.format.date(startDate, "yyyyMMddHHmm");
                var endDbDate = $.format.date(endDate, "yyyyMMddHHmm");

                if(startDbDate < currDbDate || endDbDate < currDbDate) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.task.startEndTime.notless"/>');
                    return false;
                }

                var repeatInterval = $("#repeatInterval").val();
                if (repeatInterval == '') {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.repeatIntervalNotExist"/>');
                   return false;
                }

                if (isNaN(repeatInterval)) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.repeatIntervalNumeric"/>');
                   return false;
                }
                
                if(repeatInterval < 0)  {
                	Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.task.check.repeatInterval.range"/>');
                    return false;
                }
            }
            return true;
        }

        function validationDateCnt(value) {
            var checkCnt = 0;
            if(value == "" || value == null) {
                checkCnt++;
            }
            return checkCnt;
        }

        function buttonControl(add) {
            if (editAuth == "true") {
                if (add == 'add') {
                    $("#addBtn").hide();
                    $("#updateBtn").hide();
                    $("#delBtn").hide();
                    $("#startBtn").hide();
                    $("#runBtn").hide();
                    $("#okBtn").show();
                    $("#cancelBtn").show();

                    $("#jobName").toggleClass("transonly", false);
                    $("#jobName").removeAttr("readonly");
                    $("#jobClassName").toggleClass("transonly", false);
                    $("#jobClassName").removeAttr("readonly");
                    $("#triggerName").toggleClass("transonly", false);
                    $("#triggerName").removeAttr("readonly");
                } else {
                    $("#addBtn").show();
                    $("#updateBtn").show();
                    $("#delBtn").show();
                    $("#startBtn").show();
                    $("#runBtn").show();
                    $("#okBtn").hide();
                    $("#cancelBtn").hide();

                    $("#jobName").toggleClass("transonly", true);
                    $("#jobName").attr("readonly", true);
                    $("#jobClassName").toggleClass("transonly", true);
                    $("#jobClassName").attr("readonly", true);
                    $("#triggerName").toggleClass("transonly", true);
                    $("#triggerName").attr("readonly", true);
                }
            }
        }

        var originJobName = null;
        var originJobClassName = null;
        var originJobDescription = null;
        var originJobGroupType = null;
        var originJobGroup = null;
        
        var originTriggerName = null;
        var originCronExpression = null;
        var originRepeatInterval = null;
		var originStartTimeMin = null;
		var originStartTimeHour = null;
		var originStartTimeDay = null;
		var originStartTimeMon = null;
		var originStartTimeYear = null;
		var originEndTimeMin = null;
		var originEndTimeHour = null;
		var originEndTimeDay = null;
		var originEndTimeMon = null;
		var originEndTimeYear = null;
        var originRepeatCount = null;
        var originRepeatInterval = null;

        var originCronSec = null;
        var originCronMin = null;
        var originCronHour = null;
        var originCronDay = null;
        var originCronMon = null;
        var originCronDoW = null;

        var originCron = null;
        var originExp = null;
        // add button
        function readyAddJobTrigger() {
            originJobName = $("#jobName").val();
            originJobClassName = $("#jobClassName").val();
            originJobDescription = $("#jobDescription").val();

            originJobGroupType = $("#groupType").val();
            originJobGroup = $("#group").val();

            originTriggerName = $("#triggerName").val();
            originCronExpression = $("#cronExpression").val();
            originRepeatInterval = $("#repeatInterval").val();
            
            originStartTimeMin = $("#simpleStartTime_min").val();
            originStartTimeHour = $("#simpleStartTime_hou").val();
            originStartTimeDay = $("#simpleStartTime_day").val();
            originStartTimeMon = $("#simpleStartTime_mon").val();
            originStartTimeYear = $("#simpleStartTime_year").val();
            
            originEndTimeMin = $("#simpleEndTime_min").val();
            originEndTimeHour = $("#simpleEndTime_hou").val();
            originEndTimeDay = $("#simpleEndTime_day").val();
            originEndTimeMon = $("#simpleEndTime_mon").val();
            originEndTimeYear = $("#simpleEndTime_year").val();

            originRepeatCount = $("#repeatCount").val();
            
            originCronSec = $("#cronExpression_sec").val();
            originCronMin = $("#cronExpression_min").val();
            originCronHour = $("#cronExpression_hou").val();
            originCronDay = $("#cronExpression_day").val();
            originCronMon = $("#cronExpression_mon").val();
            originCronDoW = $("#cronExpression_dayOfWeek").val();

            originCron = $("#cron").val();
            originExp = $("#exp:checked").val();

            //$("input[name=rList]").filter(function() {if (this.checked) return this;}).val()

/*            $("#simpleStartTime_min").val('');
            $("#simpleStartTime_hou").val('');
            $("#simpleStartTime_day").val('');
            $("#simpleStartTime_mon").val('');
            $("#simpleStartTime_year").val('');

            $("#simpleEndTime_min").val('');
            $("#simpleEndTime_hou").val('');
            $("#simpleEndTime_day").val('');
            $("#simpleEndTime_mon").val('');
            $("#simpleEndTime_year").val('');

            $("#repeatCount").val('');
            $("#repeatInterval").val('');

            $("#triggerName").val('');
            $("#cronExpression_sec").val('*');
            $("#cronExpression_min").val('*');
            $("#cronExpression_hou").val('*');
            $("#cronExpression_day").val('*');
            $("#cronExpression_mon").val('*');
            $("#cronExpression_dayOfWeek").val('?');
            $("#cronExpression").val('');
            $("#repeatInterval").val('');
            $("#cron").val('');  */
            //$("#add").val('<fmt:message key="aimir.button.register"/>');
            $(":input:radio").filter("input[value='C']").attr("checked", "checked");

            $("#groupType").val("-").attr("selected","selected");
            $("#groupType").selectbox();
            getGroupCombo('', '');

            buttonControl('add');

            deleteSubDataFunc();
        }

        // cancel button
        function cancelAddJobTrigger() {
            $("#jobName").val(originJobName);
            $("#jobClassName").val(originJobClassName);
            $("#jobDescription").val(originJobDescription);
            $("#groupType").val(originJobGroupType).attr("selected","selected");
            $("#groupType").selectbox();
            getGroupCombo(originJobGroupType, originJobGroup);

            $("#simpleStartTime_min").val(originStartTimeMin);
            $("#simpleStartTime_hou").val(originStartTimeHour);
            $("#simpleStartTime_day").val(originStartTimeDay);
            $("#simpleStartTime_mon").val(originStartTimeMon);
            $("#simpleStartTime_year").val(originStartTimeYear);
            
            $("#simpleEndTime_min").val(originEndTimeMin);
            $("#simpleEndTime_hou").val(originEndTimeHour);
            $("#simpleEndTime_day").val(originEndTimeDay);
            $("#simpleEndTime_mon").val(originEndTimeMon);
            $("#simpleEndTime_year").val(originEndTimeYear);

            $("#repeatCount").val(originRepeatCount);

            $("#cronExpression_sec").val(originCronSec);
            $("#cronExpression_min").val(originCronMin);
            $("#cronExpression_hou").val(originCronHour);
            $("#cronExpression_day").val(originCronDay);
            $("#cronExpression_mon").val(originCronMon);
            $("#cronExpression_dayOfWeek").val(originCronDoW);

            $("#triggerName").val(originTriggerName);
            $("#cronExpression").val(originCronExpression);
            $("#repeatInterval").val(originRepeatInterval);
            $("#cron").val(originCron);
            $(":input:radio").filter("input[value='"+originExp+"']").attr("checked", "checked");

            buttonControl('');
        }

        function directRunJob() {
            var jobName = $("#jobName").val();
            var triggerName = $("#triggerName").val();

            $.getJSON('${ctx}/gadget/system/schedule/directRunJob.do', 
                    {jobName : jobName,
                     loginId : loginId,
                     triggerName : triggerName},
                    function( json ) {
                        /*
                        var jsonData = json.result;

                        if (jsonData == true) {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save"/>');
                        } else {
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.save.error"/>');
                        }
                        */
                    });
        }
		
		

    </script>
</head>

<body>
<div class="gadget_body">
    <!-- 좌측 job 그리드 시작 -->

    <div style="width:500px; float:left;">
        <div class="margin-t10px"></div>
        <div id="jobSummaryGridDiv" style="width: 100%; margin-bottom: 30px"></div>
        <div id="jobGridDiv" style="width: 100%;"></div> 
    </div>
    <!-- 좌측 job 그리드 끝 -->
    <div id='helpDiv'></div>  
    <!-- 우측 job panel 시작 -->
    <div style="margin-left:540px;width:auto">
        <table class="table_detail">
            <tr>
                <th><label class="check"><fmt:message key="aimir.jobName"/></label></th>
                <td><input type="text" id="jobName" name="jobName" style="width:300px;"/><!-- <span id="jobNameView"></span> --></td>
            </tr>
            <tr>
                <th><label class="check"><fmt:message key="aimir.jobClass"/></label></th>
                <td><input type="text" id="jobClassName" name="jobClassName" style="width:300px;"/><!-- <span id="jobClassNameView"></span> --></td>
            </tr>
            <tr>
                <th><label class="check"><fmt:message key="aimir.description"/></label></th>
                <td>
                    <textarea id="jobDescription" name="jobDescription" style="width:300px; height:30px;"></textarea>
                </td>
            </tr>
        </table>

		<div id="triggerGridDiv" style="width: 100%"></div>       
        <div class="expression">
        <table class="table_detail">
            <tr>
                <td colspan="2"><label class="check"><fmt:message key="aimir.scheduleName"/>:</label></td>
                <td colspan="2"><input type="text" id="triggerName" name="triggerName" style="width:300px"></td>
            </tr>
            <tr>
                <td colspan="2" ><label class="check"><fmt:message key="aimir.grouptype"/>:</label></td>
                <td>
                    <select id="groupType" name="groupType" style="width:120px;"></select>
                </td>
                <td>
                    <select id="group" name="group" style="width:170px;"></select>
                </td>
            </tr>
            <tr>
                <td><input type="radio" id="exp" name="exp" value='S' class="radio_space3"></td>
                <td><fmt:message key="aimir.simpleExpression"/></td>
                <td></td>
                <td></td>
            </tr>
            <tr>
				<td colspan="3">&nbsp;&nbsp;&nbsp;<label class="check"><fmt:message key='aimir.starttime'/></td>        
				<td colspan="3"><label class="check"><fmt:message key='aimir.loadmgmt.endtime'/></td>    
            </tr>
            <tr>
            	<td colspan="3">
                   <span style="width:41px;">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.min" /></span>
                   <span style="width:45px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.hour" /></span>
                   <span style="width:45px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.day" /></span>
                   <span style="width:45px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.day.mon" /></span>
                   <span style="width:85px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="aimir.year" /></span>
                </td>  
                <td colspan="3">
                   <span style="width:41px;"><fmt:message key="aimir.min" /></span>
                   <span style="width:45px;">&nbsp;<fmt:message key="aimir.hour" /></span>
                   <span style="width:45px;">&nbsp;<fmt:message key="aimir.day" /></span>
                   <span style="width:45px;">&nbsp;<fmt:message key="aimir.day.mon" /></span>
                   <span style="width:85px;">&nbsp;<fmt:message key="aimir.year" /></span>
                </td>  
            </tr>
            <tr >
            	<td></td>
                <td colspan="2">
                       <span class="padding-r10px"><input type="text" id="simpleStartTime_min" name="simpleStartTime_min" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="simpleStartTime_hou" name="simpleStartTime_hou" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="simpleStartTime_day" name="simpleStartTime_day" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="simpleStartTime_mon" name="simpleStartTime_mon" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="simpleStartTime_year" name="simpleStartTime_year" style="width:30px;"/></span>
                </td>
                <td colspan="2">
                       <span class="padding-r10px"><input type="text" id="simpleEndTime_min" name="simpleEndTime_min" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="simpleEndTime_hou" name="simpleEndTime_hou" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="simpleEndTime_day" name="simpleEndTime_day" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="simpleEndTime_mon" name="simpleEndTime_mon" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="simpleEndTime_year" name="simpleEndTime_year" style="width:30px;"/></span>
                </td>
            </tr>
            <tr>
                <td></td>
                <td class="padding-r20px"><fmt:message key="aimir.repeatCount"/></td>
                <td><input type="text" id="repeatCount" name="repeatCount"></td>
            </tr>
            <tr>
                <td></td>
                <td class="padding-r20px"><em class="icon_star"></em><fmt:message key="aimir.repeatInterval"/></td>
                <td><input type="text" id="repeatInterval" name="repeatInterval"></td>
                <td><a id="playBtn" href="#;"><span id="runBtn" class="btn_play"></span></a></td><!-- class="btn_stop"은 멈춤 이미지 -->
            </tr>
            <tr>
                <td><input type="radio" id="exp" name="exp" value='C' class="radio_space3"></td>
                <td><fmt:message key="aimir.cronExpression"/></td>
                <td><a href="javascript:makeHelpWindow();" title="How to set cron expression!"><label class="icon_help"></label></a></td>
                <td></td>              
            </tr>
            <tr>
                <td><input type="hidden" id="cronExpression" name="cronExpression"></td>
                <td colspan="3">
                   <span style="width:40px;"><em class="icon_star"></em><fmt:message key="aimir.sec" /></span>
                   <span style="width:40px;"><em class="icon_star"></em><fmt:message key="aimir.min" /></span>
                   <span style="width:40px;">&nbsp;<em class="icon_star"></em><fmt:message key="aimir.hour" /></span>
                   <span style="width:40px;">&nbsp;&nbsp;&nbsp;<em class="icon_star"></em><fmt:message key="aimir.day" /></span>
                   <span style="width:40px;">&nbsp;&nbsp;&nbsp;&nbsp;<em class="icon_star"></em><fmt:message key="aimir.day.mon" /></span>
                   <span style="width:120px;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<em class="icon_star"></em><fmt:message key="aimir.dayofweek" /></span>
                </td>          
            </tr>
            <tr >
                <td></td>
                <td colspan="3">
                       <span class="padding-r10px"><input type="text" id="cronExpression_sec" name="cronExpression_sec" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="cronExpression_min" name="cronExpression_min" style="width:30px;"/></span>                       
                       <span class="padding-r10px"><input type="text" id="cronExpression_hou" name="cronExpression_hou" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="cronExpression_day" name="cronExpression_day" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="cronExpression_mon" name="cronExpression_mon" style="width:30px;"/></span>
                       <span class="padding-r10px"><input type="text" id="cronExpression_dayOfWeek" name="cronExpression_dayOfWeek" style="width:30px;"/></span>
                   
                </td>
            </tr>
            <tr>
                <td colspan="2"><label class="check"><fmt:message key="aimir.subData"/></label></td>
                <td colspan="2"><button id="subDataPlusButton" style="width:50px;">+</button></td>
            </tr>
            <tr>
                <td colspan="4"><table id="InputsWrapper"></table></td>
            </tr>

            <tr>
                <td></td>
                <td></td>
                <td></td>                
                <td style="float:right;">
                    <input type="hidden" id="cron" name="cron"/>
                    <input type="hidden" id="jobRow" name="jobRow"/>
                    <input type="hidden" id="jobMaxRow" name="jobMaxRow"/>
                    <input type="hidden" id="triggerRow" name="triggerRow"/>
                    <input type="hidden" id="triggerMaxRow" name="triggerMaxRow"/>
                    <input type="hidden" id="pauseStatus" name="pauseStatus"/>
                    <div id="saveBtnList">
                        <span id="addBtn"    class="am_button margin-r5"><a href="#;" id="add"><fmt:message key="aimir.add"/></a></span>
                        <span id="updateBtn" class="am_button margin-r5"><a href="#;" id="update"><fmt:message key="aimir.update"/></a></span>
                        <span id="delBtn"    class="am_button margin-r5"><a href="#;" id="delete"><fmt:message key="aimir.button.delete"/></a></span>
                        <span id="startBtn"  class="am_button"><a href="#;" id="pause"><fmt:message key="aimir.stop"/></a></span>
                        <span id="okBtn"     class="am_button margin-r5" style="display:none;"><a href="#;" id="ok"><fmt:message key="aimir.ok"/></a></span>
                        <span id="cancelBtn" class="am_button" style="display:none;"><a href="#;" id="cancel"><fmt:message key="aimir.cancel"/></a></span>
                    </div>
                </td>
            </tr>            
        </table>
        </div>

        <label class="check"><fmt:message key="aimir.schedulerLog" /></label>
        <div class="margin-t10px"></div>
        <div class="border-blue overflow_hidden">
            <div class="margin10px overflow_hidden" >
                <table class="search">
                    <tr>
                        <th style="padding:0px 5px 0px 0px;"><fmt:message key="aimir.date"/>:</th>
                        <th style="padding:0px 0px 0px 0px;"><input id="startDate" type="text" readonly="readonly" style="width:55px; text-align:center;"></th>
                        <th style="padding:0px 5px 0px 5px;">~</th>
                        <td style="padding:0px 0px 0px 0px;"><input id="endDate" type="text" readonly="readonly" style="width:55px; text-align:center;"></td>
                        <th style="padding:4px 5px 4px 20px;"><fmt:message key="aimir.scheduleName"/>:</th>
                        <td style="padding:0px 0px 0px 0px;"><select id="triggerNameSelect" name="triggerNameSelect" style="width:100px;"></select></td>
                        <th style="padding:4px 5px 4px 10px;"> <fmt:message key="aimir.result"/>:</th>
                        <td style="padding:0px 0px 0px 0px;">
                            <!-- <select id="result" name="result" style="width:80px;">
                                <option value="-1">All</option>
                                <option value="0">SUCCESS</option>
                                <option value="1">FAIL</option>
                                <option value="2">INVALID_PARAMETER</option>
                                <option value="3">COMMUNICATION_FAIL</option>
                            </select> -->
                            <select id="result" name="result" style="width:100px;"></select>
                        </td>
                        <td style="padding:4px 0px 4px 5px;">
                            <span class="am_button" ><a href="#;" onClick="getScheduleResultLogByJobName();" id="search"><fmt:message key="aimir.button.search"/></a></span>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="dashedline"></div>
           
            <div class="gadget_body">
                <div id="logGridDiv"></div>
                <div id="progressbar"></div>
            </div>
            <div id='errorMeterListDiv'></div>
            <div id='helpDiv'></div>   
        </div>
        <!-- 우측 job panel 끝 -->

    </div>

</div>
</body>
</html>