
<%--
    @ SLA Monitoring Gadget For S-Project
    @ Set Metering Schedule to Location Groups(DSO).

--%>

<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
		 contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="PRAGMA" content="NO-CACHE">
	<meta http-equiv="Expires" content="-1">
	<!-- STYLE -->
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
	<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >

		<style type="text/css">
			/* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
			TABLE{border-collapse: collapse; width:auto;}

            .remove {
				background-image:url(../../images/allOff.gif) !important;
			}
			.accept {
				background-image:url(../../images/allOn.png) !important;
			}

			@media screen and (-webkit-min-device-pixel-ratio:0) {
				.x-grid3-row td.x-grid3-cell {
					padding-left: 0px;
					padding-right: 0px;
				}
			}
			/* ext-js grid header 정렬 */
			.x-grid3-hd-inner{
				text-align: center !important;
				font-weight: bold;
			}
			/* 그리드의 정렬 및 컬럼 옵션 메뉴를 제거 */
			.x-grid3-hd-btn {display: none; visibility: hidden;}
		</style>
	<!-- LIB -->
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery-ajaxQueue.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <!-- location(DSO)-->
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/tree/location.tree.js"></script>

</head>
<body>

<!-- SCRIPT -->
<script type="text/javascript" charset="utf-8">

    // Tab Initialize
    // tabs 0=hidden, 1=show
    var tabs     = {hourly:0,daily:0,period:0,weekly:1,monthly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0}; // weekly tab만 사용
    // tabNames can be changed to other string
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    // User(Role) Information
    var supplierId = "${supplierId}";
    var operatorId = "${operatorId}";
    // Max Meter Limit
    var maxNumber = 0;
    // EXTJS AJAX - Timeout setting (120 seconds)
    var extAjaxTimeout = 120000;
    // AJAX QUEUE
    var cmdQueue = undefined;
    // Selected Group&Command Names
    var selectedGroupData = undefined;
    // Target Group Name, Target Command Name, Target Command Parameter
    var targetGrpId = "";
    var targetCmdName = "";
    var targetCmdParam = "";
    var targetCmdArrayParam = undefined;


    //location(DSO) 가져오기
    $(function() {
        locationTreeGoGo('treeDivA', 'searchWord', 'sLocationId');
    });

    // Document Ready
    $(document).ready(function () {
        $.ajaxSetup({
            async: false
        });
        // Get User Session
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.loginId != ""){
                        loginId = json.loginId;
                    }
                    if(json.maxMeters != null && isNaN(json.maxMeters) == false){
                        maxNumber = parseInt(json.maxMeters);
                    }
                });

        $('#slaFunctionTab').tabs();
		
        // SLA TAB
        $(function() {
            $('#_slaTab').bind('click', function(event) {

            });
        });
        
        // SCHEDULE TAB
        $(function() {
            $('#_scheduleTab').bind('click', function(event) {
                scheduleTabInit();
            });
        });

        $('#_weekly').hide(); // weekly검색($('#weekly'))은 남겨두고 탭($('#_weekly'))만 숨김
        searchList();
    });

    // sla tab init
    function slaTabInit(){

    }
    
    // change the group command div to input element
    function changeGroupCommandDiv(reqDiv){
    	// 잘못된 입력값인지 확인
    	if(reqDiv == undefined){
    		Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.invalidArgument'/>');
            return false;
    	}
    	
    	// 현재 화면을 먼저 다 숨긴 다음에..
    	$('#modemCommandDiv').hide();
    	$('#dcuCommandDiv').hide();
    	$('#emptyCommandDiv').hide();
    	// Clear the text in input-elements.
    	$("input[name]='name'").val('');
    	
    	// Set the same height between right and left side.        
        var tabHeight = $('#rightSide').height();
        $('#leftSide').height(tabHeight);
    	
    	// 입력에 맞는 화면을 보여준다
    	if(reqDiv == 'modemCommandDiv' || reqDiv == 'Meter' || reqDiv == 'Modem'){    		
    		$('#modemCommandDiv').show();
    	}else if(reqDiv == 'dcuCommandDiv' || reqDiv == 'DCU'){
    		$('#dcuCommandDiv').show();    
    	}else{
    		$('#emptyCommandDiv').show();
    	}
    	        	
    }

    // schedule tab init
    function scheduleTabInit(){
        // 1. Hide group-command Div
        $('#commandResultDiv').hide();
        $('#strategyDiv').show();
        // 2. Group Name List
        getGroupNameGrid();
        // 3. Schedule List
        getStrategyGrid();
        // 4. Set group-command div to empty.
        changeGroupCommandDiv('emptyCommandDiv');
        // 5. Clear the command param
        selectedGroupData = undefined;
        
    }

    // search버튼 클릭 시 / 페이지 시작할 때 (s)
    var flag = false;
    function searchList(){
        if(flag == false) // 첫 searchList()에서 DateType.WEEKLY의 값이 공백이므로  getSearchDate()를 실시하지 않는다.
            flag=true;
        else
            getSearchDate(DateType.WEEKLY, flag); // 년/월/주로 시작일자(startDate)와 종료일자(endDate)를 구하는 함수
        getSlaGrid();
    }
    // search버튼 클릭 시 / 페이지 시작할 때 (e)


    // SLA 그리드 (s)
	var slaStore;
	var slaCol;
	var slaGrid;
    var slaGridOn = false;
	var getSlaGrid = function(){
		var grWidth = $('#slaGridDiv').width()-2;
        var pageSize = 30;
        
        slaStore = new Ext.data.JsonStore({
            autoLoad : true,
            url: "${ctx}/gadget/system/sla/getSLAList.do",
            baseParams : {
                'supplierId' : supplierId,  
                'dso' : $('#sLocationId').val(),
                'msa' : $('#msa').val(),
                'startDate' : $('#searchStartDate').val(),
                'endDate' : $('#searchEndDate').val(),
            },
            root: 'rtnStr',
            fields: [
                     'dso','msa','installedMeter','collectedRate','slaMeter','slaRate'
                     ],
        });
        
        slaCol = new Ext.grid.ColumnModel({
            columns: [
                      {header: 'DSO', dataIndex: 'dso',},
                      {header: 'MSA', dataIndex: 'msa', },
                      {header: 'Installed Meter', dataIndex: 'installedMeter', },
                      {header: 'Collected Rate(%)', dataIndex: 'collectedRate', },
                      {header: 'SLA Meter', dataIndex: 'slaMeter', },
                      {header: 'SLA Rate(%)', dataIndex: 'slaRate', },
                  ],
                   defaults: {
                	   align : 'center',
                	   sortable : false,
                       menuDisable : true,
                       hideable : false,
                       //width : 120
                  },
         });

        if(!slaGridOn){
            $('#slaGridDiv').html(' ');
            slaGrid = new Ext.grid.GridPanel({
                store : slaStore,
                colModel : slaCol,
                sm : new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(smd, row, rec) {
                            var data = rec.data;
                        }
                    }
                }),
                renderTo : 'slaGridDiv',
                autoScroll : true,
                height : 614,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    forceFit : true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },

                // 필요 시 paging bar 추가해야 함
            });
            slaGridOn = true;
        }else{
            slaGrid.setWidth(grWidth);
            slaGrid.reconfigure(slaStore, slaCol);
        }

	}
    // SLA 그리드(e)

    // Group Name List -- Grid  (s)
    var groupNameStore;
    var groupNameCol;
    var groupNameGrid;
    var groupNameGridOn = false;
    var getGroupNameGrid = function(){
        var grWidth = $('#groupListGrid').width()-2;

        groupNameStore = new Ext.data.JsonStore({
            autoLoad : true,
            url: "${ctx}/gadget/system/sla/getGroupNameList2.do",
            baseParams : {
                'supplierId' : supplierId,
                'operatorId' : operatorId,
                // groupType : 'Meter'
            },
            root: 'result',
            fields: ['groupName','groupType','memCount'],
        });

        groupNameCol = new Ext.grid.ColumnModel({
            columns: [
                {header: 'Group', dataIndex: 'groupName', width:40},
                {header: 'Type', dataIndex: 'groupType', width:30},
                {header: 'Member Number', dataIndex: 'memCount', width:30 }
            ],
            defaults: {
                align : 'center',
                sortable : true,
                menuDisable : true,
                hideable : false,
            },
        });

        if(!groupNameGridOn){
            $('#groupListGrid').html(' ');
            groupNameGrid = new Ext.grid.GridPanel({
                store : groupNameStore,
                colModel : groupNameCol,
                sm : new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(smd, row, rec) {
                            selectedGroupData = rec.json;
                            // 선택한 grouptype에 맞는 command div를 전환.
                            changeGroupCommandDiv(selectedGroupData.groupType);                            
                        }
                    }
                }),
                renderTo : 'groupListGrid',
                autoScroll : true,
                height : 264,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    forceFit : true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },

                // 필요 시 paging bar 추가해야 함
            });
            groupNameGridOn = true;
        }else{
            groupNameGrid.setWidth(grWidth);
            groupNameGrid.reconfigure(groupNameStore, groupNameCol);
        }

    }
    // Group Name List -- Grid  (e)

    // Strategy List -- Grid (s)
    var strategyStore;
    var strategyCol;
    var strategyGrid;
    var strategyGridOn = false;
    var getStrategyGrid = function(){
        var grWidth = $('#strategyGrid').width()-10;
        var pageSize = 30;

        strategyStore = new Ext.data.JsonStore({
            autoLoad : true,
            url: "${ctx}/gadget/system/sla/getStrategyList.do",
            baseParams : {
                'supplierId' : supplierId,
            },
            root: 'result',
            fields: [
                'groupId','groupName','configName','configValue','previous','updateDate','createDate','loginId'
            ],
        });

        strategyCol = new Ext.grid.ColumnModel({
            columns: [
                {header: 'GROUP', dataIndex: 'groupName',},
                {header: 'Schedule', dataIndex: 'configName', },
                {header: 'Value', dataIndex: 'configValue', },
                {header: 'Previous', dataIndex: 'previous', },
                {header: 'Update Date', dataIndex: 'updateDate', },
                {header: 'Create Date', dataIndex: 'createDate', },
                {header: 'User', dataIndex: 'loginId', },
            ],
            defaults: {
                align : 'center',
                sortable : false,
                menuDisable : true,
                hideable : false,
                //width : 120
            },
        });

        if(!strategyGridOn){
            $('#strategyGrid').html(' ');
            strategyGrid = new Ext.grid.GridPanel({
                store : strategyStore,
                colModel : strategyCol,
                sm : new Ext.grid.RowSelectionModel({
                    singleSelect:true,
                    listeners: {
                        rowselect: function(smd, row, rec) {
                            var data = rec.data;
                        }
                    }
                }),
                renderTo : 'strategyGrid',
                autoScroll : true,
                height : 614,
                width : grWidth,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'Loading...'
                },
                viewConfig: {
                    forceFit : true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },

                // 필요 시 paging bar 추가해야 함
            });
            strategyGridOn = true;
        }else{
            strategyGrid.setWidth(grWidth);
            strategyGrid.reconfigure(strategyStore, strategyCol);
        }

    }
    // Strategy List -- Grid (e)

    // Group-Command Exec, Result   (s)
    var cmdResultStore;
    var cmdResultCol;
    var cmdResultGrid;
    var cmdResultGridOn = false;
    function drawCommandResult(cmdGrpId, cmdFunc, cmdParam, cmdArrayParam){
        // set last parameter
        targetGrpId = cmdGrpId;
        targetCmdName = cmdFunc;
        targetCmdParam = cmdParam;
        targetCmdArrayParam = cmdArrayParam;

        var grWidth = $('#commandResultGrid').width()-5;

        cmdResultStore = new Ext.data.JsonStore({
            autoLoad : true,
            url : '${ctx}/gadget/system/sla/getGroupMeterList.do',
            type : 'POST',
            root : 'result',
            baseParams : {
                groupId : cmdGrpId
            },
            fields : [ {
                //group.member
                name : '1'
            }, {
                //group.id
                name : '0'
            } ]
        });

        var colWidth = grWidth/5;
        cmdResultCol = new Ext.grid.ColumnModel({
            defaults : {
                align : 'center',
                sortable : true
            },
            columns : [ {
                id : "deviceId",
                width : colWidth,
                header : "Device ID",
                dataIndex : "1"
            }, {
                header : "Status",
                width : colWidth*2,
                dataIndex : "status"
            }, {
                header : "Result",
                width : colWidth,
                dataIndex : "view"
            } ]
        });

        if(!cmdResultGridOn){
            cmdResultGrid = new Ext.grid.GridPanel({
                store : cmdResultStore,
                colModel : cmdResultCol,
                width : grWidth,
                height : 550,
                renderTo : 'commandResultGrid',
                viewConfig : {
                    forceFit: true,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },
                listeners: {
                    'rowdblclick' : function(grid, index, e){
                        // Fires when a row is double clicked
                        var rowData = grid.getStore().data.get(index).data;
                        var info = '* Modem [' + rowData[1] + '] \n'+
                                '* Status [' + rowData.status + '] \n'+
                                        '* Detail [' + rowData.view + '] \n';
                        console.log(info);
                    }
                }
            });
            cmdResultGridOn = true;
        }else{
            cmdResultGrid.setWidth(grWidth);
            cmdResultGrid.reconfigure(cmdResultStore, cmdResultCol);
        }
    }
    // Group-Command Exec, Result   (e)

    // AjaxQueue - Metering Interval
    function grpMtrIntervalService(reqValue){
        var ajaxSuccessCount = 0;
        var ajaxFailCount = 0;
        var storeCount = cmdResultStore.getCount();
        //Requests asynchronously.
        $.ajaxSetup({
            async : true
        });

        //Add image loading in the first item
        cmdResultStore.getAt(0).set('status',
                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

        for (var i=0; i < storeCount; i++){
            // ajax queue, plugin for sequential processing
            cmdQueue = $.ajaxQueue({
                type: "POST",
                url: "${ctx}/gadget/device/command/cmdGroupMeteringInterval.do",
                data: {
                    mdsId : cmdResultStore.getAt(i).get('1'),
                    loginId : loginId,
                    requestValue : reqValue
                },
                error: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    cmdResultGrid.getView().focusRow(q);
                    var record = cmdResultStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.intervalStatus);
                    record.set('view', returnData.cmdResult);
                    ajaxFailCount+=1;
                    if((q+1) < storeCount){
                        cmdResultStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                    }

                },
                success: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    cmdResultGrid.getView().focusRow(q);
                    var record = cmdResultStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.intervalStatus);
                    record.set('view', returnData.cmdResult);
                    ajaxSuccessCount+=1;
                    if ((q+1) < storeCount){
                        cmdResultStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {// 맨 마지막 동작일때
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                    }
                } //~success

            });
        } //~for

    } //~grpMtrIntervalService

    // AjaxQueue - Transmit Frequency
    function grpTrsFrequencyService(reqValue){
        var ajaxSuccessCount = 0;
        var ajaxFailCount = 0;
        var storeCount = cmdResultStore.getCount();
        //Requests asynchronously.
        $.ajaxSetup({
            async : true
        });

        //Add image loading in the first item
        cmdResultStore.getAt(0).set('status',
                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

        for (var i=0; i < storeCount; i++){
            // ajax queue, plugin for sequential processing
            cmdQueue = $.ajaxQueue({
                type: "POST",
                url: "${ctx}/gadget/device/command/cmdGroupTransmitFrequency.do",
                data: {
                    mdsId : cmdResultStore.getAt(i).get('1'),
                    loginId : loginId,
                    requestValue : reqValue
                },
                error: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    cmdResultGrid.getView().focusRow(q);
                    var record = cmdResultStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.transmitStatus);
                    record.set('view', returnData.cmdResult);
                    ajaxFailCount+=1;
                    if((q+1) < storeCount){
                        cmdResultStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                    }

                },
                success: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    cmdResultGrid.getView().focusRow(q);
                    var record = cmdResultStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.transmitStatus);
                    record.set('view', returnData.cmdResult);
                    ajaxSuccessCount+=1;
                    if ((q+1) < storeCount){
                        cmdResultStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {// 맨 마지막 동작일때
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                    }
                } //~success

            });
        } //~for

    } //~grpTrsFrequencyService


    // AjaxQueue - Retry Count
    function grpRetryCountService(reqValue){
        var ajaxSuccessCount = 0;
        var ajaxFailCount = 0;
        var storeCount = cmdResultStore.getCount();
        //Requests asynchronously.
        $.ajaxSetup({
            async : true
        });

        //Add image loading in the first item
        cmdResultStore.getAt(0).set('status',
                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

        for (var i=0; i < storeCount; i++){
            // ajax queue, plugin for sequential processing
            cmdQueue = $.ajaxQueue({
                type: "POST",
                url: "${ctx}/gadget/device/command/cmdGroupRetryCount.do",
                data: {
                    mdsId : cmdResultStore.getAt(i).get('1'),
                    loginId : loginId,
                    requestValue : reqValue
                },
                error: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    cmdResultGrid.getView().focusRow(q);
                    var record = cmdResultStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.status);
                    record.set('view', returnData.cmdResult);
                    ajaxFailCount+=1;
                    if((q+1) < storeCount){
                        cmdResultStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                    }

                },
                success: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    cmdResultGrid.getView().focusRow(q);
                    var record = cmdResultStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.status);
                    record.set('view', returnData.cmdResult);
                    ajaxSuccessCount+=1;
                    if ((q+1) < storeCount){
                        cmdResultStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {// 맨 마지막 동작일때
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                        console.log('total['+storeCount+'] success['+ajaxSuccessCount+'] fail['+ajaxFailCount+']');
                    }
                } //~success

            });
        } //~for
    }
    
    
 // AjaxQueue - DCU type: Set the schedule
    function grpDcuSetScheduleService(reqValue){
        var ajaxSuccessCount = 0;
        var ajaxFailCount = 0;
        var storeCount = cmdResultStore.getCount();
        //Requests asynchronously.
        $.ajaxSetup({
            async : true
        });

        //Add image loading in the first item
        cmdResultStore.getAt(0).set('status',
                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

        for (var i=0; i < storeCount; i++){
            // ajax queue, plugin for sequential processing
            cmdQueue = $.ajaxQueue({
                type: "POST",
                url: "${ctx}/gadget/device/command/cmdDcuGroupSetSchedule.do",
                data: {
                	'target' : cmdResultStore.getAt(i).get('1'),
                    'loginId' : loginId,
                    'meteringcondition' : reqValue[1],
                    'meteringtask' : reqValue[2],
                    'meteringsuspend' : reqValue[3],
                    'recoverycondition' : reqValue[4],
                    'recoverytask' : reqValue[5],
                    'recoverysuspend' : reqValue[6],
                    'uploadcondition' : reqValue[7],
                    'uploadtask' : reqValue[8],
                    'uploadsuspend' : reqValue[9],
                    'retrycondition' : reqValue[10]
                },
                error: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    cmdResultGrid.getView().focusRow(q);
                    var record = cmdResultStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.status);
                    record.set('view', returnData.cmdResult);
                    ajaxFailCount+=1;
                    if((q+1) < storeCount){
                        cmdResultStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                    }

                },
                success: function(returnData){
                    var q = ajaxSuccessCount + ajaxFailCount;
                    cmdResultGrid.getView().focusRow(q);
                    var record = cmdResultStore.getAt(q);

                    record.set('status', returnData.rtnStr +' : '+ returnData.status);
                    record.set('view', returnData.cmdResult);
                    ajaxSuccessCount+=1;
                    if ((q+1) < storeCount){
                        cmdResultStore.getAt(q+1).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');
                    }
                    if (window.ajaxQueueCount[cmdQueue] == 1) {// 맨 마지막 동작일때
                        $.ajaxSetup({
                            async : true
                        });
                        /**
                         *  New Strategy Log
                         */
                    }
                } //~success

            });
        } //~for

    } //~grpDcuSetScheduleService
    

    // EventHandler - CANCEL(prev) button
    function prevBtn(){
        // Switch #strategyGrid to #commandResultGrid on RightSide
        $('#commandResultDiv').hide();
        $('#strategyDiv').show();
        // draw schedule-list grid
        getStrategyGrid();
    }
    // EventHandler - EXECUTE(exec) button
    function execBtn(){
        // check Group-Command Parameters
        if(selectedGroupData == undefined || selectedGroupData.length < 1){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.alert.groupMgmt.msg17'/>');
            return false;
        }
        // check MaxMeter
        if(maxNumber != 0 &&  maxNumber < selectedGroupData.memCount){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.meterlimit"/>');
            return false;
        }else{
            // captcha & runmaxNumber
            var captitle = "<fmt:message key='aimir.confirmGroupMeteringSchedule'/>";
            CaptchaPanel2(captitle);
        }
    }

    // EventHandler - Metering Interval's Update Button
    function setMtrIntervalAction(){
        // Whether group is selected or not
        if(selectedGroupData == undefined){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.alert.groupMgmt.msg17'/>');
            return false;
        }
        // User Input
        var itvHour = parseInt($('#intervalHour').val().trim());
        var itvMin = parseInt($('#intervalMinute').val().trim());
        var itvSec = parseInt($('#intervalSecond').val().trim());
        var itvTotal = 0;
        // Validation Check
        if(isNaN(itvHour) || isNaN(itvMin) || isNaN(itvSec)){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.onlyNumber'/>');
            return false;
        }else{
            itvTotal = parseInt(itvHour)*3600 + parseInt(itvMin)*60 + parseInt(itvSec);
            Ext.Msg.confirm('<fmt:message key="aimir.button.confirm"/>',
                    'Update MeteringInterval to ['+itvTotal+'] seconds',
                    function(btn, txt){
                        if(btn != 'yes'){
                            return false;
                        }else{
                            // Switch #strategyGrid to #commandResultGrid on RightSide
                            $('#strategyDiv').hide();
                            $('#commandResultDiv').show();
                            // draw group-command grid
                            drawCommandResult(selectedGroupData.groupId, 'MeteringInterval', itvTotal, '');
                            // write group name & command name
                            $('#selectedGroup').text('* Group Name : '+selectedGroupData.groupName);
                            $('#selectedCommand').text('* Command Name : Metering Interval');
                        } //~confirm inner function
                    }); //~Ext.Msg.confirm
        }
    } //~eventhandler

    // EventHandler - Transmit Frequency's Update Button
    function setTrsFrequencyAction(){
        // Whether group is selected or not
        if(selectedGroupData == undefined){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.alert.groupMgmt.msg17'/>');
            return false;
        }
        // User Input
        var frqHour = parseInt($('#transmitHour').val().trim());
        var frqMin = parseInt($('#transmitMinute').val().trim());
        var frqSec = parseInt($('#transmitSecond').val().trim());
        var frqTotal = 0;
        // Validation Check
        if(isNaN(frqHour) || isNaN(frqMin) || isNaN(frqSec)){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.onlyNumber'/>');
            return false;
        }else{
            frqTotal = parseInt(frqHour)*3600 + parseInt(frqMin)*60 + parseInt(frqSec);
            Ext.Msg.confirm('<fmt:message key="aimir.button.confirm"/>',
                    'Update TransmitFrequency to ['+frqTotal+'] seconds',
                    function(btn, txt){
                        if(btn != 'yes'){
                            return false;
                        }else{
                            // Switch #strategyGrid to #commandResultGrid on RightSide
                            $('#strategyDiv').hide();
                            $('#commandResultDiv').show();
                            // draw group-command grid
                            drawCommandResult(selectedGroupData.groupId, 'TransmitFrequency', frqTotal, '');
                            // write group name & command name
                            $('#selectedGroup').text('* Group Name : '+selectedGroupData.groupName);
                            $('#selectedCommand').text('* Command Name : Transmit Frequency');
                        } //~confirm inner function
                    }); //~Ext.Msg.confirm
        }
    } //~eventhandler

    // EventHandler - Retry Count's Update Button
    function setRetryCountAction(){
        // Whether group is selected or not
        if(selectedGroupData == undefined){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.alert.groupMgmt.msg17'/>');
            return false;
        }
        // User Input
        var retryInput = $('#retryCountInput').val().trim();
        // Validation check
        if(isNaN(retryInput)==true || retryInput.length < 1){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.onlyNumber'/>');
            return false;
        }else{
            Ext.Msg.confirm('<fmt:message key="aimir.button.confirm"/>',
                    'Update RetryCount to ['+retryInput+'] ',
                    function(btn, txt){
                        if(btn != 'yes'){
                            return false;
                        }else{
                            // Switch #strategyGrid to #commandResultGrid on RightSide
                            $('#strategyDiv').hide();
                            $('#commandResultDiv').show();
                            // draw group-command grid
                            drawCommandResult(selectedGroupData.groupId, 'RetryCount', retryInput,'');
                            // write group name & command name
                            $('#selectedGroup').text('* Group Name : '+selectedGroupData.groupName);
                            $('#selectedCommand').text('* Command Name : Retry Count');
                        } //~confirm inner function
                    }); //~Ext.Msg.confirm
        }
    } //~eventhandler
    
    
    // EventHandler - Get single DCU's Schedule Button
    function getDcuScheduleAction(){
    	    	
    	Ext.MessageBox.prompt("Search DCU's Schedule", 'Please enter the DCU-ID<br>',
    			function(btn, numTxt) {
		    		// 집중기 sys_id (textbox)		
		    		var sysId = numTxt.trim();
    				if(btn=='ok'){
    					// 입력 값 검증
    					if(sysId.length < 1){
    						Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.alert.inputDCUId'/>');
    						return false;
    					}
    					// Ajax 정의    			        
    			        Ext.define('dcuIdAjax', {
    			            extend: 'Ext.data.Connection',
    			            singleton: true,
    			            constructor : function(config){
    			                this.callParent([config]);
    			                this.on("beforerequest", function(){
                                    Ext.MessageBox.wait("Get Response From CommandGW...", '<fmt:message key="aimir.info"/>', {
                                        text: '<fmt:message key="aimir.maximum"/> '+ extAjaxTimeout/1000 + 's...',
                                        scope: this,
                                    });
    			                });
    			                this.on("requestcomplete", function(){
    			                    Ext.MessageBox.hide();
    			                });
    			            }
    			        });
    					// Ajax call (timeout : 120 seconds -> important)
    			        dcuIdAjax.request({
    			            url : "${ctx}/gadget/device/command/cmdMcuGetSchedule2.do",
    			            method : 'POST',
                            timeout : extAjaxTimeout,
    			            params: {
    			                target : sysId,
    			                loginId : loginId,    			                
    			            },
    			            success: function (result, request){
    			                //폼 윈도우를 닫고, 결과 처리
    			                Ext.MessageBox.hide();
    			                var jsonData = Ext.util.JSON.decode( result.responseText );
    			                if(jsonData.status != undefined){
    			                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',jsonData.status);
    			                	 // Metering
    		                        $('#varMeteringSchedule').val(jsonData.metering_condition);
    		                        $('#varMeteringTask').val(jsonData.metering_task);
    		                        if(jsonData.metering_suspend != undefined && jsonData.metering_suspend =='true'){
    		                        	$("input:checkbox[id='varMeteringSuspend']").attr('checked',true);
    		                        }else{
    		                        	$("input:checkbox[id='varMeteringSuspend']").attr('checked',false);
    		                        }
    		                        // Recovery
    		                        $('#varRecoverySchedule').val(jsonData.recovery_condition);
    		                        $('#varRecoveryTask').val(jsonData.recovery_task);
    		                        if(jsonData.recovery_suspend != undefined && jsonData.recovery_suspend =='true'){
    		                        	$("input:checkbox[id='varRecoverySuspend']").attr('checked',true);
    		                        }else{
    		                        	$("input:checkbox[id='varRecoverySuspend']").attr('checked',false);
    		                        }
    		                        // Upload
    		                        $('#varUploadSchedule').val(jsonData.upload_condition);
    		                        $('#varUploadTask').val(jsonData.upload_task);
    		                        if(jsonData.upload_suspend != undefined && jsonData.upload_suspend =='true'){
    		                        	$("input:checkbox[id='varUploadSuspend']").attr('checked',true);
    		                        }else{
    		                        	$("input:checkbox[id='varUploadSuspend']").attr('checked',false);
    		                        }
    		                        // 재시도 시간
    		                        $('#varRetryDefault').val(jsonData.retry_condition);
    			                }else{
    			                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',jsonData.rtnStr);
    			                }
    			            },
    			            failure: function(result, request){
    			                //폼 윈도우를 닫고, 결과 처리
    			                Ext.MessageBox.hide();
    			                // 타임아웃 구분
                                if(result.isTimeout){                                   
                                	var trm = " "+"Response Timeout ("+request.timeout/1000+"s)";
                                    Ext.Msg.alert('<fmt:message key='aimir.message'/>',trm);
                                }else{
                                    var erm = " "+request.statusText;
                                	Ext.Msg.alert('<fmt:message key='aimir.message'/>',erm);
                                }
    			            }
    			        });
    				}	
    				}, this, false, ''); //<prompt> scope=this, multiline=false, default=1   
    				    	    	    
    	
    } //~eventhandler
    
    // EventHandler - DCU's Schedule Set Button
    function setDcuScheduleAction(){
    	// Whether group is selected or not
        if(selectedGroupData == undefined){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.alert.groupMgmt.msg17'/>');
            return false;
        }
    	// User Input
        var varMeteringSchedule  = $('#varMeteringSchedule').val().trim();
    	var varMeteringTask		 = $('#varMeteringTask').val().trim();
    	var varMeteringSuspend	 = $("input:checkbox[id='varMeteringSuspend']").is(":checked");
        var varRecoverySchedule  = $('#varRecoverySchedule').val().trim(); 
        var varRecoveryTask 	 = $('#varRecoveryTask').val().trim();
        var varRecoverySuspend 	 = $("input:checkbox[id='varRecoverySuspend']").is(":checked");
        var varUploadSchedule 	 = $('#varUploadSchedule').val().trim();
        var varUploadTask		 = $('#varUploadTask').val().trim();
        var varUploadSuspend	 = $("input:checkbox[id='varUploadSuspend']").is(":checked");
        var varRetryDefault 	 = $('#varRetryDefault').val().trim();              
		
        // Set Params
        var scheduleParams = Array();
        scheduleParams[0] = Math.random();
        scheduleParams[1] = varMeteringSchedule;
        scheduleParams[2] = varMeteringTask;
        scheduleParams[3] = varMeteringSuspend;
        scheduleParams[4] = varRecoverySchedule;
        scheduleParams[5] = varRecoveryTask;
        scheduleParams[6] = varRecoverySuspend;
        scheduleParams[7] = varUploadSchedule;        
        scheduleParams[8] = varUploadTask;  
        scheduleParams[9] = varUploadSuspend;  
        scheduleParams[10] = varRetryDefault;  
        
        // Validation Check
        if (isNaN(varRetryDefault) == true) {
        	// retry interval is presented as number
        	Ext.Msg.alert('Error', '[Retry Interval Count] should be number.');
			return false;
        }else {
        	// Ready for Execute..
        	Ext.Msg.confirm('<fmt:message key="aimir.button.confirm"/>',
                    'Update DCU Schedule to new value',
                    function(btn, txt){
                        if(btn != 'yes'){
                            return false;
                        }else{
                            // Switch #strategyGrid to #commandResultGrid on RightSide
                            $('#strategyDiv').hide();
                            $('#commandResultDiv').show();
                            // draw group-command grid
                            drawCommandResult(selectedGroupData.groupId, 'DcuSetSchedule', '',scheduleParams);
                            // write group name & command name
                            $('#selectedGroup').text('* Group Name : '+selectedGroupData.groupName);
                            $('#selectedCommand').text('* Command Name : DCU Set Schedule');
                        } //~confirm inner function
                    }); //~Ext.Msg.confirm        	
        }               
    } //~eventhandler


    // Captcha Window
    var captchacount=1;
    var incorrectCodeCheck=false;
    function CaptchaPanel2(captitle) {
        if (Ext.getCmp('captchaWindowPanel2')) {
            Ext.getCmp('captchaWindowPanel2').close();
        }
        var captchaFormPanel2 = new Ext.form.FormPanel(
                {
                    id : 'formpanel',
                    defaultType : 'fieldset',
                    bodyStyle : 'padding:1px 1px 1px 1px',
                    frame : true,
                    items : [
                        {
                            xtype : 'panel',
                            html : '<center><img src="${ctx}/CaptChaImg.jsp?rand='
                            + Math.random() + '"/></center></br>',
                            align : 'left'
                        },
                        {
                            xtype : 'textfield',
                            id : 'captchaCode',
                            fieldLabel : '<fmt:message key="aimir.captchaCode" />',
                            emptyText : '<fmt:message key="aimir.enterTheCode" />',
                            disabled : false,
                        },
                        {
                            xtype : 'label',
                            id : 'infolabel',
                            style : {
                                background : '#ffff00'
                            },
                            text : '*<fmt:message key="aimir.incorrectCode" />',
                            hidden : true
                        } ],
                    buttons : [
                        {
                            text : '<fmt:message key="aimir.refresh" />',
                            listeners : {
                                click : function(btn, e) {
                                    //captchaFormPanel2.reload();
                                    CaptchaPanel2(captitle);
                                }
                            }
                        },
                        {
                            text : '<fmt:message key="aimir.submit" />',
                            listeners : {
                                click : function(btn, e) {
                                    if (5 <= captchacount) {
                                        window.open('${ctx}/admin/logout.do',"_parent").parent.close();
                                    }
                                    $.ajax({url : '${ctx}/gadget/report/CaptchaSubmit.do',
                                        type : 'POST',
                                        dataType : 'json',
                                        data : 'answer='+ $('#captchaCode').val(),
                                        async : false,
                                        success : function(data) {
                                            if (data.capcahResult == "true") {
                                                captchacount = 1;
                                                if (targetCmdName == "MeteringInterval") {
                                                    grpMtrIntervalService(targetCmdParam);
                                                } else if (targetCmdName == "RetryCount") {
                                                    grpRetryCountService(targetCmdParam);
                                                } else if (targetCmdName == "TransmitFrequency") {
                                                    grpTrsFrequencyService(targetCmdParam);
                                                } else if (targetCmdName == "DcuSetSchedule") {
                                                	// DCU는 Array Parameter를 사용.
                                                	grpDcuSetScheduleService(targetCmdArrayParam);
                                                }
                                                else {
                                                    Ext.Msg.alert("", "Not implemented !!");
                                                }
                                                captchaWindow2.close();
                                            } else {
                                                captchacount++;
                                                incorrectCodeCheck = true;
                                                CaptchaPanel2(captitle);
                                            }
                                        }
                                    });
                                }
                            }
                        },
                        {
                            text : '<fmt:message key="aimir.cancel" />',
                            listeners : {
                                click : function(btn, e) {
                                    Ext.getCmp('captchaWindowPanel2').close();
                                }
                            }
                        } ]
                });

        var captchaWindow2 = new Ext.Window({
            id : 'captchaWindowPanel2',
            title : captitle,
            pageX : 500,
            pageY : 100,
            height : 206,
            width : 300,
            layout : 'fit',
            bodyStyle : 'padding: 10px 10px 10px 10px;',
            items : [ captchaFormPanel2 ],
            resizable : false
        });

        captchaWindow2.show();
        // show error message
        if (incorrectCodeCheck == true) {
            Ext.getCmp('infolabel').setVisible(true);
            Ext.getCmp('captchaCode').focus(true, 100);
            incorrectCodeCheck = false;
        }
    }

</script>

<!-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -->
<!-- location(DSO) (s)-->
<div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
	<div id="treeDivA"></div>
</div>
<!-- location(DSO) (e)-->
	

    <!-- tab(s) -->
    <div id="slaFunctionTab">
        <ul>
            <li><a class='topTabType' href="#slaTabDiv" id="_slaTab">Monitoring</a></li>
            <li><a class='topTabType' href="#scheduleTabDiv" id="_scheduleTab">Schedule</a></li>
        </ul>


<!-- 1. SLA Monitoring tab (s)-->
	<div id="slaTabDiv" class="tabcontentsbox">
		<!-- common date(s) -->	
	    <div class="search-bg-withouttabs">
        	<div class="dayoptions-bt">
        	<%@ include file="/gadget/commonDateTabButtonType.jsp"%>
        <div class="dashedline"></div>
        <!-- common date(e) -->
        
        </div>
			<table class="searching">
				<tbody><tr>
					<td class="padding-r10px"></td>
					<!-- DSO (s)-->
					<td class="withinput" style="width: 20px">DSO</td>
                    <td><input type="text" id="searchWord" name="searchWord" style="width:120px"></td>
					<td><input type="hidden" id="sLocationId" name="locationID" value="-1" /></td>
					<!-- DSO (e)-->
					
					<!-- MSA (s)-->
                    <td class="padding-r10px">
					<td class="withinput" style="width: 30px">MSA</td>
					<td><input id="msa" type="text" style="width: 100px;"></td>
					<!-- MSA (e)-->
					<!-- Search Button -->
					<td class="padding-r10px"></td>
					<td><em id ="weeklySearch" class="am_button"><a href="javascript:searchList();" class="on">Search</a></em></td>
				</tr>
				</tbody>
				</table>
		</div>

        <ul class="basic-ul">
            <li class="basic-li redbold11pt withinput">Metering Rate Base Time:  </li>
        </ul>
	 	<!-- SLA 그리드가 그려지는 부분 -->
	 	<div id="slaGridDiv" class="margin10px">
	 		The Grid will appear within here. 
	 	</div>
	</div>
<!-- SLA Monitoring tab (e)-->
	

<!-- 2. Schedule tab (s)-->
	<div id="scheduleTabDiv" class="tabcontentsbox">
	    <!-- [O| ] LEFT SIDE : Group List & Setting -->
        <div id="leftSide" class="width-39 margin-t10px floatleft border_blu">
            <div class="margin10px "> <!-- wrapper -->
            <div><label class="check ">Group List</label></div>

                <!-- ExtGrid:group list(location type only)-->
                <div id="groupListGrid" class="margin-t10px  ">
                    The Grid will appear within here.
                </div>
                <br>
            <!-- -- HTML: 0. Empty -- -- -- -- -->
            	<div id="emptyCommandDiv" class="margin-t10px  ">
            		<p><label class="check"><fmt:message key="gadget.system009" /></label></p>
            		<br>
            		<p><fmt:message key="aimir.alert.groupMgmt.msg17" /></p>
            	</div>  <!-- ~empty div -->
            	
       		<!-- -- HTML: 1. DCU group command(metering interval, retry count) -- -- -- -- -->
                <div id="dcuCommandDiv" class="margin-t10px  ">
                	<!-- metering schedule -->
                	<ul class="width margin-t10px">
                	
	                	<label class="check"><fmt:message key="aimir.meteringschedule" /> & Option</label>
						<div class=" margin10px" >
							<input id='varMeteringSchedule' name='inputbox' type='text' class="values greenbold" style="width:350px;">
							<li class="padding-t3px"><input id='varMeteringSuspend' type="checkbox"><label class="darkgraybold11pt">Stop Schedule</label></li>
							<input id='varMeteringTask' name='inputbox' type='text' class="values greenbold" style="width:350px;" value="TASK"><br>
						</div>													
						<br>	
						
						<label class="check"><fmt:message key="aimir.recoveryschedule" /> & Option</label>
						<div class=" margin10px">
							<input id='varRecoverySchedule' name='inputbox' type='text' class="values greenbold" style="width:350px;">							
							<li class="padding-t3px"><input id='varRecoverySuspend' type="checkbox"><label class="darkgraybold11pt">Stop Schedule</label></li>
							<input id='varRecoveryTask' name='inputbox' type='text' value="TASK" class="values greenbold" style="width:350px;"><br>			
						</div>																		
						<br>	
						
						<label class="check"><fmt:message key="aimir.uploadschedule" /> & Option</label>
						<div class=" margin10px">
							<input id='varUploadSchedule' name='inputbox' type='text' class="values greenbold" style="width:350px;">
							<li class="padding-t3px"><input id='varUploadSuspend' type="checkbox"><label class="darkgraybold11pt">Stop Schedule</label></li>
							<input id='varUploadTask' name='inputbox' type='text' value="TASK" class="values greenbold" style="width:350px;"><br>
						</div>                                
						<br>	         
						                                                  
						<label class="check"><fmt:message key="aimir.retryIntervalTime" /></label>
                        <div class=" margin10px">
                            <input id='varRetryDefault' name='inputbox' type='text' class="values greenbold" style="width:200px;">
                        </div>
		                <br>
		                <br>   	                        
                        <div class="headspace" style="width:100%;">
                        <em class="btn_bluegreen"><a href="javascript:getDcuScheduleAction()"><fmt:message key="aimir.button.search" /></a></em>
                            <em class="btn_bluegreen"><a href="javascript:setDcuScheduleAction()"><fmt:message key="aimir.update"/></a></em>
                        </div>

                    </ul>
                    
                    
                </div> <!--  ~DCU group command -->
                
            <!-- -- HTML: 2. Modem/Meter group command(metering interval, retry count) -- -- -- -- -->
                <div id="modemCommandDiv" class="margin-t10px ">
                    <!-- Metering Interval (S) -->
                    <ul class="width margin-t10px">

                        <div><label class="check">Metering Interval</label></div>

                        <table class="wfree margin-t10px">
                            <tr>
                                <th><label class="blue12pt">Hour </label></th>
                                <td><input id="intervalHour" name='inputbox' type="text" style="width:35px;"/></td>
                                <th><label class="blue12pt"> Minute </label></th>
                                <td><input id="intervalMinute" name='inputbox' type="text" style="width:35px;"/></td>
                                <th><label class="blue12pt"> Second </label></th>
                                <td><input id="intervalSecond" name='inputbox' type="text" style="width:35px;"/></td>
                            </tr>
                        </table>
                        
                        <div class="margin-t2px margin-b3px">
                            <label class="blue12pt">* Read LP data at specific period. (Usually every 60 minutes.)</label>
                        </div>
                        <div class="headspace" style="width:100%;">
                            <em class="btn_bluegreen"><a href="javascript:setMtrIntervalAction()"><fmt:message key="aimir.update"/></a></em>
                        </div>

                    </ul>
                    <br><br>
                    <!-- Transmit Frequency (S) -->
                    <ul class="width margin-t10px">

                        <div><label class="check">Transmit Frequency</label></div>

                        <table class="wfree margin-t10px">
                            <tr>
                                <th><label class="blue12pt">Hour </label></th>
                                <td><input id="transmitHour" name='inputbox' type="text" style="width:35px;"/></td>
                                <th><label class="blue12pt"> Minute </label></th>
                                <td><input id="transmitMinute" name='inputbox' type="text" style="width:35px;"/></td>
                                <th><label class="blue12pt"> Second </label></th>
                                <td><input id="transmitSecond" name='inputbox' type="text" style="width:35px;"/></td>
                            </tr>
                        </table>
                        
                        <div class="margin-t2px margin-b3px">
                            <label class="blue12pt">* Upload LP data at specific period. (Usually every 60 minutes.)</label>
                        </div>
                        <div class="headspace" style="width:100%;">
                            <em class="btn_bluegreen"><a href="javascript:setTrsFrequencyAction()"><fmt:message key="aimir.update"/></a></em>
                        </div>

                    </ul>
                    <br><br>
                    <!-- Retry Count (S) -->
                    <ul class="width margin-t10px">

                        <div><label class="check">Retry Count</label></div>
                        <table class="wfree margin-t10px">
                            <tr>
                                <th><label class="blue12pt">Count </label></th>
                                <td><input id="retryCountInput" name='inputbox' type="text" style="width:50px;" ></td>
                            </tr>
                        </table>
                       
                        <div class="margin-t2px margin-b3px"><label class="blue12pt">* The number of re-upload.(Default 3 times)</label></div>
                        <div class="headspace" style="width:100%;">
                            <em class="btn_bluegreen"><a href="javascript:setRetryCountAction()"><fmt:message key="aimir.update"/></a></em>
                        </div>
                    </ul>
                </div>
            </div> <!-- ~wrapper -->
        </div>

        <!-- [ |O] RIGHT SIDE : Strategy List & Execution -->
        <div id="rightSide" class="width-60 margin-t10px floatright border_blu">
            <div class="margin10px"> <!-- wrapper -->
        <!-- Result list, Schedule list (switch) -->
        <!--[1] Schedule-->
            <div class="margin-t5px" id="strategyDiv">
                <div><label class="check ">Group Schedule List</label></div>
                <div class="margin-t10px" id="strategyGrid"></div>
            </div>
        <!--[2] Result-->
            <div class="margin-t5px" id="commandResultDiv">
                <div><label class="check ">Group-Command Result</label></div>
                <ul class="margin-t5px">
                    <p class="heat14pt" id="selectedGroup"></p>
                    <p class="heat14pt" id="selectedCommand"></p>
                </ul>

                <!-- Shows when execute the command only (update action) -->
                <ul class="margin-t10px">
                    <em name="updateActionBtn" id="prevBtn" class="am_button margin-t2px"><a href="javascript:prevBtn();">CANCEL</a></em>
                    <em name="updateActionBtn" id="execBtn" class="am_button margin-t2px"><a href="javascript:execBtn();">EXECUTION</a></em>
                </ul>
                <div class="margin-t5px" id="commandResultGrid"></div>
            </div>


            </div>
        </div>
	</div>	
<!-- Schedule tab (e)-->


    </div>
    <!-- tab(e) -->



</body>
</html>