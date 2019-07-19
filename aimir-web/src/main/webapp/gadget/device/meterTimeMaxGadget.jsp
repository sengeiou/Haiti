<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Meter Time Management MaxGadget</title>

<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
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
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold;
    }
    #fcMeterTimeDiffChartId{
    	margin-left:calc(50% - 450px) !important;
    }
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery-ui-1.7.2.min.js"></script>
<script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery-ajaxQueue.js"></script>

<script type="text/javascript" charset="utf-8">/*<![CDATA[*/
                                                          
	var loginId="";
	
    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do',
        function(json) {
            if(json.supplierId != ""){
                loginId = json.loginId;
            }
        }
    );

	var meterTimeDiffChartDataXml;
	var meterTimeDiffChart;
	var meterTimeDiffComplianceChartDataXml;
	var meterTimeDiffComplianceChart;

	var meterTimeSyncLogLeftChartDataXml;
	var meterTimeSyncLogLeftChart;
	var meterTimeSyncLogRightChartDataXml;
	var meterTimeSyncLogRightChart;

    var supplierId = ${supplierId};
    var chromeColAdd = 2;
    var sForm = undefined;
    var renderObjects;
    // Command 권한
    var cmdAuth = "${cmdAuth}";

    $(document).ready(function() {
        $('#_period').hide();
        if (cmdAuth == "true") {
            $("#timeSyncBtn").show();
            $("#selMcuLabel").show();
            $("#thresholdBtn").show();
        } else {
            $("#timeSyncBtn").hide();
            $("#selMcuLabel").hide();
            $("#thresholdBtn").hide();
        }
    });

    var BASE = {
        window: {
            layout: 'anchor',
            plain: true,
            autoHeight: true,
            width: 450,
            closeAction: 'hide',
            modal: true,
            items: []           
        },
        form: {   
            frame: {
                labelWidth : 190,
                id: 'setting_threshold_panel',
                frame: true,
                items: [],
                 buttons: [{
                    text: "<fmt:message key='aimir.save2'/>"
                },{
                    text: "<fmt:message key='aimir.cancel'/>"
                }]
            },
            combo: {
                width: 180,
                typeAhead: true,
                triggerAction: 'all',
                lazyRender:true,
                editable: false,
                mode: 'local',
                valueField: 'id',
                displayField: 'name',       
                allowBlank: false
            },
            textfield: {
                width: 180,
                allowBlank: false
            },
            datefield: {
                width: 180,
                editable: false,
                format: 'Y/m/d',
                allowBlank: false
            }
        }
    };
    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:0,period:1,weekly:0,monthly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

   
    $(function(){

        $("#meterTimeMax").tabs();

        // combo설정
        $("#timeDiffTimeDiff").selectbox();
        $("#timeDiffTimeType").selectbox();
        $("#timeDiffCompliance").selectbox();

        $("#timeSyncLogMethodView ").selectbox();
        $("#timeSyncLogStatus").selectbox();
        $("#timeSyncLogTimeDiff").selectbox();
        $("#timeSyncLogTimeType").selectbox();

        $("#timeThresHoldMcuCommState").selectbox();
        $("#timeThresHoldTimeDiffType").selectbox();

        // 모뎀 타입
       /*  $.getJSON('${ctx}/gadget/system/getChildCode.do'
                , {'code' : '1.1.1'}
                , function (returnData){
                    $('#timeThresHoldMcuSysType').loadSelect(returnData.code);
                    $('#timeThresHoldMcuSysType').selectbox();

                }); 

		locationTreeGoGo('treeDivA', 'timeThresHoldLocationIdText', 'timeThresHoldLocationId');*/
        getMeterTimeDiffGrid();
		updateMeterTimeDiff();
        /* sForm = renderSettingThresholdForm({
            handler: {
                        saveHandler: function() {
                             SettingThresholdForm();
                        },
                        cancelHandler: function() {
                            Ext.getCmp(sForm.id).hide();
                        }                       
                    }
         }); */
    });
    
    $(function() { $('#_timeDiff')     .bind('click',function(event) { timeDiffSearch(); } ); });

    $(function() { $('#_timeSyncLog')  .bind('click',function(event) { timeSyncLogSearch();} ); });

    /* $(function() { $('#_timeThreshold').bind('click',function(event) { thresholdSearch(); } ); }); */


    // Meter Time Resize 관련 JS ---------------------------------------------

    $(window).resize(function() {
            fcChartRender();

            if(!(meterTimeDiffGrid === undefined)){
                meterTimeDiffGrid.destroy();
            }
            meterTimeDiffGridOn = false;
            getMeterTimeDiffGrid();

            if(!(meterTimeSyncGrid === undefined)){
                meterTimeSyncGrid.destroy();                
            }
            meterTimeSyncGridOn = false;
            getMeterTimeSyncGrid();

            /* meterTimeThresholdGrid.destroy();
            meterTimeThresholdGridOn = false;
            getMeterTimeThresholdGrid(); */

    });
 	function fcChartRender() {
 	 	var chartWidthTab1 = 450;

 		var chartWidthTab2 = $('#fcLeftChartDiv').width();
 		if(chartWidthTab2 == 0) {
 	 		if($('#timeDiff').width() > 0)
 	 			chartWidthTab2 = ($('#timeDiff').width() / 100) * 45;
 	 		else if($('#timeThreshold').width() > 0)
 	 			chartWidthTab2 = ($('#timeThreshold').width() / 100) * 45;
 		}

 		if($('#fcMeterTimeDiffChart').is(':visible')) {
 			meterTimeDiffChartRenderer();

 		}

 		if($('#fcMeterTimeDiffComplianceChart').is(':visible')) {
 			meterTimeDiffComplianceChartRenderer();
 		}

 		if($('#fcLeftChartDiv').is(':visible')) {
			fcLeftChartRenderer();
		}

		if($('#fcRightChartDiv').is(':visible')) {
			fcRightChartRenderer();
		}
 	}

    //################### 1. Meter Time Difference List 탭 ###################### 
    
    //전체 조회 
    function timeDiffSearch(){
        updateMeterTimeDiff();  //차트
        getMeterTimeDiffGrid(); //그리드
    }

    //차트 그리기
     function updateMeterTimeDiff() {

        $.getJSON('${ctx}/gadget/device/getMeterTimeTimeDiffChart.do'
                ,{mcuSysId:$('#timeDiffMcuSysId').val(),
                    customerName:encodeURIComponent($('#timeDiffCustomerName').val()),
                    meterMdsId:$('#timeDiffMeterMdsId').val(),
                    contractNumber:$('#timeDiffContractNumber').val(),
                    timeDiff:$('#timeDiffTimeDiff').val(),
                    time:$('#timeDiffTime').val(),
                    timeType:$('#timeDiffTimeType').val(),
                    compliance:$('#timeDiffCompliance').val(),
                    supplierId:supplierId}
            ,function(json) {
                 var chartData = json.chartData;
                meterTimeDiffChartDataXml = "<chart "
                    + "caption='<fmt:message key='aimir.current3'/> <fmt:message key='aimir.status'/>' " 
                    + "chartLeftMargin='5' "
                    + "chartRightMargin='5' "
                    + "chartTopMargin='5' "
                    + "chartBottomMargin='10' "
                    + "pieRadius='120' "
                    + "showZeroPies='1' "
                    + "showLabels='0' "
                    + "showValues='0' "
                    + "showLegend='1' "
                    + "showpercentintooltip='0' "
                    + "legendPosition='RIGHT' "
                    + "manageLabelOverflow='1' "
                    + "numberSuffix='  ' "
                    + fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Pie3D 
                    + ">";
                 var labels = "<set label='<fmt:message key='aimir.normal'/>' value='"+chartData.NORMAL+"' color='"+fChartColor_Step4[0]+"' />"
               			+ "<set label='<fmt:message key='aimir.bems.facilityMgmt.unknown'/>' value='"+chartData.DIFF_UNKNOWN+"' color='8833CB' />"
                        + "<set label='1~12 <fmt:message key='aimir.hour'/>' value='"+chartData.DIFF_1+"' color='"+fChartColor_Step4[1]+"' />"
                        + "<set label='12~24 <fmt:message key='aimir.hour'/>' value='"+chartData.DIFF_12+"' color='"+fChartColor_Step4[2]+"' />"
                        + "<set label='24 <fmt:message key='aimir.hour'/> <fmt:message key='aimir.over'/>' value='"+chartData.DIFF_24+"' color='"+fChartColor_Step4[3]+"' />";
 			
 				
						
                if(chartData.NORMAL == null && chartData.DIFF_1 == null && chartData.DIFF_12 == null && chartData.DIFF_24 == null) {
                     labels = "<set label='' value='1' color='E9E9E9' toolText='' />"
                 }
                                     
                 meterTimeDiffChartDataXml += labels + "</chart>";
                meterTimeDiffChartRenderer();
            }
        );

        $.getJSON('${ctx}/gadget/device/getMeterTimeTimeDiffComplianceChart.do'
                ,{mcuSysId:$('#timeDiffMcuSysId').val(),
                    customerName:encodeURIComponent($('#timeDiffCustomerName').val()),
                    meterMdsId:$('#timeDiffMeterMdsId').val(),
                    contractNumber:$('#timeDiffContractNumber').val(),
                    timeDiff:$('#timeDiffTimeDiff').val(),
                    time:$('#timeDiffTime').val(),
                    timeType:$('#timeDiffTimeType').val(),
                    compliance:$('#timeDiffCompliance').val(),
                    supplierId:supplierId}
            ,function(json) {
                 var chartData = json.chartData;
             meterTimeDiffComplianceChartDataXml = "<chart "
                    + "caption='<fmt:message key='aimir.history'/>' " 
                    + "chartLeftMargin='5' "
                    + "chartRightMargin='5' "
                    + "chartTopMargin='5' "
                    + "chartBottomMargin='10' "
                    + "pieRadius='120' "
                    + "showZeroPies='1' "
                    + "showLabels='0' "
                    + "showValues='0' "
                    + "showLegend='1' "
                    + "showPercentInToolTip='0' "
                    + "legendPosition='RIGHT' "
                     + "manageLabelOverflow='1' "
                     + "numberSuffix='  ' "
                    + fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Pie3D 
                    + ">";

             var labels = "<set label='<fmt:message key='aimir.compliace'/>' value='"+chartData.COMPLIANCE+"' color='"+fChartColor_Step2[0]+"'/>"
                        + "<set label='<fmt:message key='aimir.notCompliance'/>' value='"+chartData.NON_COMPLIANCE+"' color='"+fChartColor_Step2[1]+"'/>";
                        
            
             if(chartData.COMPLIANCE == null && chartData.NON_COMPLIANCE == null ) {
                 labels = "<set label='' value='1' color='E9E9E9' toolText='' />"
             }
                                 
             meterTimeDiffComplianceChartDataXml += labels + "</chart>";
             
            meterTimeDiffComplianceChartRenderer();

            }
        );
    }

    function meterTimeDiffChartRenderer() {
    	if ( FusionCharts( "fcMeterTimeDiffChartId" ) ) FusionCharts( "fcMeterTimeDiffChartId" ).dispose();
    	console.log(meterTimeDiffChartDataXml);
    	meterTimeDiffChart = new FusionCharts({
    		id: 'fcMeterTimeDiffChartId',
			type: 'pie3d',
			renderAt : 'fcMeterTimeDiffChart',
			width : '450',
			height : '240',
			dataSource : meterTimeDiffChartDataXml
		}).render();
    	
        /* meterTimeDiffChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "fcMeterTimeDiffChartId", 450, 240, "0", "0");
        meterTimeDiffChart.setDataXML(meterTimeDiffChartDataXml);
        meterTimeDiffChart.setTransparent("transparent");
        meterTimeDiffChart.render("fcMeterTimeDiffChart"); */
    }

    function meterTimeDiffComplianceChartRenderer() {
    	if ( FusionCharts( "fcMeterTimeDiffComplianceChartId" ) ) FusionCharts( "fcMeterTimeDiffComplianceChartId" ).dispose();
    	console.log(meterTimeDiffComplianceChartDataXml);

    	meterTimeDiffComplianceChart = new FusionCharts({
    		id: 'fcMeterTimeDiffComplianceChartId',
			type: 'pie3d',
			renderAt : 'fcMeterTimeDiffComplianceChart',
			width : '450',
			height : '240',
			dataSource : meterTimeDiffComplianceChartDataXml
		}).render();
    	
/*         meterTimeDiffComplianceChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "fcMeterTimeDiffComplianceChartId", 450, 240, "0", "0");
        meterTimeDiffComplianceChart.setDataXML(meterTimeDiffComplianceChartDataXml);
        meterTimeDiffComplianceChart.setTransparent("transparent");
        meterTimeDiffComplianceChart.render("fcMeterTimeDiffComplianceChart"); */
    }
    //TimeDiff 조건
    function getConditionTimeDiff(){
        var condArray = new Array();

        condArray[0] = $('#timeDiffMcuSysId').val();
        condArray[1] = encodeURIComponent($('#timeDiffCustomerName').val());
        condArray[2] = $('#timeDiffMeterMdsId').val();
        condArray[3] = $('#timeDiffContractNumber').val();

        condArray[4] = $('#timeDiffTimeDiff').val();
        condArray[5] = $('#timeDiffTime').val();
        condArray[6] = $('#timeDiffTimeType').val();

        condArray[7] = $('#timeDiffCompliance').val();
        condArray[8] = supplierId;
        return condArray;
    }
    //TimeDiff 메시지
    function getMsgTimeDiffGrid(){
        var fmtMessage = new Array();
        fmtMessage[0] = "";
        fmtMessage[1] = "No";
        fmtMessage[2] = "<fmt:message key="aimir.mcu"/>";
        fmtMessage[3] = "<fmt:message key="aimir.meterid"/>";
        fmtMessage[4] = "<fmt:message key="aimir.customername"/>";
        fmtMessage[5] = "<fmt:message key="aimir.contractNumber"/>";
        fmtMessage[6] = "<fmt:message key="aimir.location"/>";
        fmtMessage[7] = "<fmt:message key="aimir.lasttimesync"/>";
        fmtMessage[8] = "<fmt:message key="aimir.timediff"/>";
        fmtMessage[9] = "<fmt:message key="aimir.excel.meterTimeDiff"/>";//엑셀타이틀
        return fmtMessage;
    }
    //TimeDiff 그리드
    var meterTimeDiffGridStore;
    var meterTimeDiffGridColModel;
    var meterTimeDiffGridOn = false;
    var meterTimeDiffGrid;
    var meterTimeDiffCheckSelModel;
    function getMeterTimeDiffGrid(){
        var arrayObj = getConditionTimeDiff();
        var fmtMessage = getMsgTimeDiffGrid();
        var width = $("#meterTimeDiffGridDiv").width(); 

         meterTimeDiffGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 20}},
            url: "${ctx}/gadget/device/getMeterTimeTimeDiffGrid.do",
            baseParams:{
                mcuSysId         : arrayObj[0],
                customerName     : arrayObj[1],
                meterMdsId       : arrayObj[2],
                contractNumber   : arrayObj[3],
                timeDiff         : arrayObj[4],
                time             : arrayObj[5],
                timeType         : arrayObj[6],
                compliance       : arrayObj[7],
                supplierId       : arrayObj[8],
                pageSize         : 20
            },
            totalProperty: 'totalCnt',
            root:'gridData',
             fields: [
            { name: 'no', type: 'String' },
            { name: 'mcuSysId', type: 'String' },
            { name: 'meterMdsID', type: 'String' },
            { name: 'customerName', type: 'String' },
            { name: 'contractNumber', type: 'String' },
            { name: 'locName', type: 'String' } ,
            { name: 'lastLinkTime', type: 'String' } ,
            { name: 'timeDiff', type: 'String' } 
            ],
            listeners: {
            beforeload: function(store, options){
            options.params || (options.params = {});
            Ext.apply(options.params, {
                          curPage: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                     });
            }
        }
        });
        
        if(meterTimeDiffGridOn == false) {
                meterTimeDiffCheckSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly:false
                });
            }

        meterTimeDiffGridColModel = new Ext.grid.ColumnModel({
            columns: [
                meterTimeDiffCheckSelModel,
                {header: fmtMessage[1], 
                   dataIndex: 'no', 
                   align:'center', 
                   width: 60 
                 }
                 ,{header: fmtMessage[2], 
                   dataIndex: 'mcuSysId', 
                   align:'center', 
                   width: 120 
                 }
                ,{header: fmtMessage[3],
                    width: 185, 
                    dataIndex: 'meterMdsID', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[4],
                    width: 185, 
                    dataIndex: 'customerName', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[5],
                    width: 185, 
                    dataIndex: 'contractNumber', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[6],
                    width: 185, 
                    dataIndex: 'locName', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[7],
                    width: 185, 
                    dataIndex: 'lastLinkTime', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[8],
                    width: 185, 
                    dataIndex: 'timeDiff', 
                    align: 'right'
                 }
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                ,width: ((width-30)/8)-chromeColAdd

            },

        });
        if (meterTimeDiffGridOn == false) {
            meterTimeDiffGrid = new Ext.grid.GridPanel({
                id: 'meterTimeDiffGrid',
                store: meterTimeDiffGridStore,
                colModel : meterTimeDiffGridColModel,
                sm: meterTimeDiffCheckSelModel,
                autoScroll: false,
                width: width,
                height: 520,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'meterTimeDiffGridDiv',
                viewConfig: {
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 20,
                    store: meterTimeDiffGridStore,
                    displayInfo: true,

                    displayMsg: ' {0} - {1} / {2}'                
                })
            });
            meterTimeDiffGridOn  = true;
        } else {
            
            meterTimeDiffGrid.setWidth(width);
            meterTimeDiffGrid.reconfigure(meterTimeDiffGridStore, meterTimeDiffGridColModel);
            var bottomToolbar = meterTimeDiffGrid.getBottomToolbar();                        
            bottomToolbar.bindStore(meterTimeDiffGridStore);
        }
    }

    // TimeDiff ok 
    var groupTimeSyncGrid;
    var groupTimeSyncStore;
    var groupTimeSyncGridOn = false;
    var groupTimeSyncWin;
    var queueName = undefined;
    var timeSyncMeterList;
    function groupTimeDiffSync(){
        //alert("timeSync");
        timeSyncMeterList = new Array();
        var array = new Array();
        var checkedArr = meterTimeDiffCheckSelModel.getSelections();

        if (checkedArr.length > 0) {
                for (var i = 0 ; i < checkedArr.length ; i++) {
                    if (checkedArr[i].get("meterMdsID") != null && checkedArr[i].get("meterMdsID") != "") {
                        timeSyncMeterList[i]=[checkedArr[i].get("meterMdsID"), checkedArr[i].get("mcuSysId")];
                        var gridData = [checkedArr[i].get("meterMdsID"), 'Processing...'];
                        array[i] = gridData;
                    }
                }
                
                this.gridH = 100 + Number(timeSyncMeterList.length * 25);
                this.winH = 200 + Number(timeSyncMeterList.length * 25);
                if (this.gridH > 600)
                    this.gridH = 600;
                if (this.winH > 700)
                    this.winH = 700;

                makeAlertWindow(array);
            }
        else {
            Ext.Msg.alert("",
                    "Please select items", null, null);
        }
       
    }
    
    //팝업 윈도우 생성
    function makeAlertWindow(array) {

        if (groupTimeSyncStore == undefined) {
            groupTimeSyncStore = new Ext.data.ArrayStore({
                fields : [ {
                    name : 'meterMds'
                }, {
                    name : 'status'
                } ]
            });
        }
        groupTimeSyncStore.loadData(array);

        var colModel = new Ext.grid.ColumnModel({
            defaults : {
                width : 685,
                height : 100,
                sortable : true
            },
            columns : [

            {
                id : "meterMds",
                width : 150,
                header : "Meter ID",
                dataIndex : "meterMds"
            },{
                header : "Result",
                width : 530,
                dataIndex : "result"
            } ]

        });

        //그리드 설정
        if (groupTimeSyncGridOn == false) {

            groupTimeSyncGrid = new Ext.grid.GridPanel({
                height : gridH,
                store : groupTimeSyncStore,
                colModel : colModel,
                width : 685
            });

            groupTimeSyncGridOn = true;
        } else {
            groupTimeSyncGrid.reconfigure(groupTimeSyncStore, colModel);
        }

        //윈도우 설정
        if (!groupTimeSyncWin) {
            groupTimeSyncWin = new Ext.Window({
                title : 'Group TimeSync',
                id : 'drAlertTimeSyncWinId',
                applyTo : 'drAlertTimeSyncWin',
                autoScroll : true,
                autoHeight : true,
                pageX : 400,
                pageY : 130,
                width : 700,
                height : winH,
                items : groupTimeSyncGrid,
                closeAction : 'hide',
                onHide : function() {
                    if (queueName == undefined)
                        return;
                    $.ajaxQueueStop(queueName);
                }
            });
        } else {
            groupTimeSyncWin.setHeight(winH);
            groupTimeSyncGrid.setHeight(gridH);
        }
        Ext.getCmp('drAlertTimeSyncWinId').show();

        //이전 데이터 초기화.
        array = new Array;
        //IE, chrome 딜래이 생기는 현상 방지
        setTimeout("timeSyncTask();", 100);
    }
    
    function timeSyncTask() {

        ajaxSuccessCount = 0;//요청 완료시 counting된다.

        //비동기 방식으로 요청한다.
        $.ajaxSetup({
            async : true
        });

        //처음 항목에 loading 이미지 추가
        groupTimeSyncStore.getAt(0).set('result',
                        'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

        for ( var i = 0; i < timeSyncMeterList.length; i++) {
            //요청을 큐에 쌓아 순차적으로 처리하는 플러그인.
             queueName = $
                    .ajaxQueue({
                        type : "GET",
                        url : '${ctx}/gadget/device/setMeterTimesync.do',
                        data : {
                            'mdsId' : timeSyncMeterList[i][0],
                            'sysId' : timeSyncMeterList[i][1],
                            'loginId' : loginId,
                        },
                        success : function(returnData) {
                            var i = ajaxSuccessCount;
                            groupTimeSyncGrid.getView().focusRow(i);
                            var record = groupTimeSyncStore.getAt(i);
                            if (returnData.rtnStr == 'Not found MCU') {
                                record.set('result', 'Not Found MCU!');
                            } else if (returnData.rtnStr == 'Not found Meter') {
                                record.set('result', 'Not Found Meter!');
                            } else {
                                record.set('result', returnData.rtnStr);
                            }
                            ajaxSuccessCount++;
                            if (timeSyncMeterList.length != ajaxSuccessCount)
                                groupTimeSyncStore
                                        .getAt(ajaxSuccessCount)
                                        .set('result',
                                             'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

                            if (window.ajaxQueueCount[queueName] == 1) // 맨 마지막 동작일때
                                $.ajaxSetup({
                                    async : true
                                });//원래 설정대로 동기방식으로 변경
                        }
                    }); 
        }

    }

    // TimeDiff Excel 출력
    var winTimeDiff;
    function exportExcelMeterTimeDiffGrid(){        
        var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage1 = new Array();
        var condition1  = new Array();

        fmtMessage1 = getMsgTimeDiffGrid();
        condition1 = getConditionTimeDiff();

        obj.condition = condition1;
        obj.fmtMessage = fmtMessage1;
        obj.url = '${ctx}/gadget/device/meterTimeDiffMaxExcelMake.do';

        if(winTimeDiff)
            winTimeDiff.close();
        winTimeDiff = window.open("${ctx}/gadget/ExcelDownloadPopup.do","MeterTimeDiffExcel", opts);
        winTimeDiff.opener.obj = obj;
    }
    
    function timeDiffType() {
        
        if($('#timeDiffTimeDiff').val() == 6) {
            $('#timeDiffSecSearch').show();
            $('#timeDiffSecSearchFmt').show();
        } else {
            $('#timeDiffTime').val("");
            $('#timeDiffSecSearch').hide();
            $('#timeDiffSecSearchFmt').hide();
        }
        
    }
    //################### Meter Time Difference List 탭 End ###################### 



    //################### 2. Meter Time Sync Log 탭 ###################### 
    //TimeSyncLog 전체 조회
    function timeSyncLogSearch(){
        $('#timeSyncLogMethod').val($('#timeSyncLogMethodView').val());
        var methodType = $('#timeSyncLogMethod').val();
        
        if(methodType == "Auto"){
            $('#timeSyncLogMethod').val("Auto");
        }else if(methodType == "Manual"){
            $('#timeSyncLogMethod').val("Manual");
        } else {
            $('#timeSyncLogMethod').val("");
        }

        timeSyncLogChart();    //차트
        getMeterTimeSyncGrid(); //그리드
    }
    //TimeSyncLog  조건
    function getConditionTimeSyncLog(){
        var condArray = new Array();

        condArray[0] = $('#timeSyncLogMcuSysId').val();
        condArray[1] = $('#timeSyncLogMeterMdsId').val();
        condArray[2] = $('#timeSyncLogOperatorId').val();

        condArray[3] = $('#timeSyncLogMethod').val();
        condArray[4] = $('#timeSyncLogStatus').val();

        condArray[5] = $('#timeSyncLogTimeDiff').val();
        condArray[6] = $('#timeSyncLogTime').val();
        condArray[7] = $('#timeSyncLogTimeType').val();

        condArray[8] = $('#searchStartDate').val();
        condArray[9] = $('#searchEndDate').val();
        condArray[10] = supplierId;

        return condArray;
    }
    //TimeSyncLog 메시지
    function getMsgTimeSyncLogGrid(){
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.orderNo"/>";
        fmtMessage[1] = "<fmt:message key="aimir.hour"/>";
        fmtMessage[2] = "<fmt:message key="aimir.bems.facilityMgmt.kind"/>";
        fmtMessage[3] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[4] = "<fmt:message key="aimir.meterid"/>";

        fmtMessage[5] = "<fmt:message key="aimir.timediff"/>";
        fmtMessage[6] = "<fmt:message key="aimir.previous"/>" + " " + "<fmt:message key="aimir.date"/>";
        fmtMessage[7] = "<fmt:message key="aimir.py.currenttime"/>";
        fmtMessage[8] = "<fmt:message key="aimir.status"/>";
        fmtMessage[9] = "<fmt:message key="aimir.user.id"/>";
        fmtMessage[10] = "<fmt:message key="aimir.excel.meterTimeSync"/>";


        return fmtMessage;
    }
    //TimeSyncLog 그리드
    var meterTimeSyncGridStore;
    var meterTimeSyncGridColModel;
    var meterTimeSyncGridOn = false;
    var meterTimeSyncGrid;
    function getMeterTimeSyncGrid(){
        var arrayObj = getConditionTimeSyncLog();
        var fmtMessage = getMsgTimeSyncLogGrid();
        var width = $("#meterTimeSyncGridDiv").width(); 

         meterTimeSyncGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/device/getMeterTimeSyncLogGrid.do",
            baseParams:{
                mcuSysId        : arrayObj[0],
                meterMdsId      : arrayObj[1],
                operatorId      : arrayObj[2],
                method          : arrayObj[3],
                status          : arrayObj[4],
                timeDiff        : arrayObj[5],
                time            : arrayObj[6],
                timeType        : arrayObj[7],
                searchStartDate : arrayObj[8],
                searchEndDate   : arrayObj[9],
                supplierId      : arrayObj[10],
                pageSize        : 10
            },
            totalProperty: 'totalCnt',
            root:'gridData',
             fields: [
            { name: 'no'        , type: 'String' },
            { name: 'writeDate' , type: 'String' },
            { name: 'method'    , type: 'String' },
            { name: 'mcuSysID'  , type: 'String' },
            { name: 'meterMdsId', type: 'String' },
            { name: 'timeDiff'  , type: 'String' },
            { name: 'previousDate', type: 'String' },
            { name: 'currentDate' , type: 'String' },
            { name: 'status'      , type: 'String' },
            { name: 'operator'    , type: 'String' } 
            ],
            listeners: {
            beforeload: function(store, options){
            options.params || (options.params = {});
            Ext.apply(options.params, {
                          curPage: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                     });
            }
        }
        });
        
        meterTimeSyncGridColModel = new Ext.grid.ColumnModel({
            columns: [
          
                {header: fmtMessage[0], 
                   dataIndex: 'no', 
                   align:'center', 
                   width: width/10-50 
                 }
                 ,{header: fmtMessage[1], 
                   dataIndex: 'writeDate', 
                   align:'center', 
                   width: width/10
                 }
                ,{header: fmtMessage[2],
                    width: width/10+10, 
                    dataIndex: 'method', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[3],
                    width: width/10, 
                    dataIndex: 'mcuSysID', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[4],
                    width: width/10, 
                    dataIndex: 'meterMdsId', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[5],
                    width: width/10, 
                    dataIndex: 'timeDiff', 
                    align: 'right'
                 }
                 ,{header: fmtMessage[6],
                    width: width/10+20, 
                    dataIndex: 'previousDate', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[7],
                    width: width/10, 
                    dataIndex: 'currentDate', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[8],
                    width: width/10, 
                    dataIndex: 'status', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[9],
                    width: width/10, 
                    dataIndex: 'operator', 
                    align: 'center'
                 }
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                ,width: ((width-10)/10)-chromeColAdd

            },

        });
        if (meterTimeSyncGridOn == false) {
            meterTimeSyncGrid = new Ext.grid.GridPanel({
                id: 'meterTimeDiffGrid',
                store: meterTimeSyncGridStore,
                colModel : meterTimeSyncGridColModel,
                autoScroll: false,
                width: width,
                height: 290,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'meterTimeSyncGridDiv',
                viewConfig: {
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: meterTimeSyncGridStore,
                    displayInfo: true,

                    displayMsg: ' {0} - {1} / {2}'                
                })
            });
            meterTimeSyncGridOn  = true;
        } else {
            
            meterTimeSyncGrid.setWidth(width);
            meterTimeSyncGrid.reconfigure(meterTimeSyncGridStore, meterTimeSyncGridColModel);
            var bottomToolbar = meterTimeSyncGrid.getBottomToolbar();                        
            bottomToolbar.bindStore(meterTimeSyncGridStore);
        }

    }
    //TimeSyncLog 차트 
    function timeSyncLogChart(){

         $('#timeSyncLogMethod').val($('#timeSyncLogMethodView').val());
         var methodType = $('#timeSyncLogMethod').val();

         console.log($('#searchStartDate').val()+" ~ "+$('#searchEndDate').val());
         if(methodType == "Auto"){
             updateMeterTimeSyncAuto();
             $('#methodLeftHead').val("Method : Auto");
             $('#methodRightHead').val("<fmt:message key='aimir.meterRankByMcu' />");
         }else if(methodType == "Manual"){
             updateMeterTimeSyncManual();
             $('#methodLeftHead').val("Method : Manual");
             $('#methodRightHead').val("<fmt:message key='aimir.meterTimeSyncStatistics' />");
         } else {
             updateMeterTimeSyncAll();
             $('#methodLeftHead').val("Method : Auto");
             $('#methodRightHead').val("Method : Manual");
         }

    }
   
    function updateMeterTimeSyncAll() {  
        $.getJSON('${ctx}/gadget/device/getMeterTimeSyncLogChart.do'
                ,{mcuSysId:$('#timeSyncLogMcuSysId').val(),
                    meterMdsId:$('#timeSyncLogMeterMdsId').val(),
                    operatorId:$('#timeSyncLogOperatorId').val(),
                    method:"Auto",
                    status:$('#timeSyncLogStatus').val(),
                    timeDiff:$('#timeSyncLogTimeDiff').val(),
                    time:$('#timeSyncLogTime').val(),
                    timeType:$('#timeSyncLogTimeType').val(),
                    searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val(),
                    supplierId:supplierId}
            ,function(json) {
                var chartData = json.chartData;
                meterTimeSyncLogLeftChartDataXml = "<chart "
                        + "yAxisName='<fmt:message key="aimir.count"/>' "
                        + "chartLeftMargin='0' "
                        + "chartRightMargin='0' "
                        + "chartTopMargin='10' "
                        + "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='1' "
                        + "labelDisplay = 'rotate' "
                        + "slantLabels='1' "
                        + "labelStep='"+parseInt(chartData.length / 7, 10)+"' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_MSColumn3D_nobg 
                        + ">";
                 var categories = "<categories>";
                 var dataset1 = "<dataset seriesName='<fmt:message key='aimir.success'/>'>";
                 var dataset2 = "<dataset seriesName='<fmt:message key='aimir.fail'/>'>";
                 var dataset3 = "<dataset seriesName='<fmt:message key='aimir.invalidArgument'/>'>";
                 var dataset4 = "<dataset seriesName='<fmt:message key='aimir.commFailure'/>'>";

                 for(var index = 0  ;index<chartData.length;index++) {
                    if(index != "indexOf") {
                        categories += "<category label='"+chartData[index].xTag+"' />";
    
                        if(chartData[index].result1 > 0) dataset1 += "<set value='"+chartData[index].result1+"' />";
                        else dataset1 += "<set value='' />";
    
                        if(chartData[index].result2 > 0) dataset2 += "<set value='"+chartData[index].result2+"' />";
                        else dataset2 += "<set value='' />";
    
                        if(chartData[index].result3 > 0) dataset3 += "<set value='"+chartData[index].result3+"' />";
                        else dataset3 += "<set value='' />";
    
                        if(chartData[index].result4 > 0) dataset4 += "<set value='"+chartData[index].result4+"' />";
                        else dataset4 += "<set value='' />";
                    }
                 }

                categories += "</categories>";
                dataset1 += "</dataset>";
                dataset2 += "</dataset>";
                dataset3 += "</dataset>";
                dataset4 += "</dataset>";                                       
                                     
                meterTimeSyncLogLeftChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";

                fcLeftChartRenderer();
            }
        );

        $.getJSON('${ctx}/gadget/device/getMeterTimeSyncLogChart.do'
                ,{mcuSysId:$('#timeSyncLogMcuSysId').val(),
                    meterMdsId:$('#timeSyncLogMeterMdsId').val(),
                    operatorId:$('#timeSyncLogOperatorId').val(),
                    method:"Manual",
                    status:$('#timeSyncLogStatus').val(),
                    timeDiff:$('#timeSyncLogTimeDiff').val(),
                    time:$('#timeSyncLogTime').val(),
                    timeType:$('#timeSyncLogTimeType').val(),
                    searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val(),
                    supplierId:supplierId}
            ,function(json) {
                var chartData = json.chartData;
                meterTimeSyncLogRightChartDataXml = "<chart "
                        + "yAxisName='<fmt:message key="aimir.count"/>' "
                        + "chartLeftMargin='0' "
                        + "chartRightMargin='0' "
                        + "chartTopMargin='10' "
                        + "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='1' "
                        + "labelDisplay = 'rotate' "
                        + "slantLabels='1' "
                        + "labelStep='"+parseInt(chartData.length / 7, 10)+"' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_MSColumn3D_nobg 
                        + ">";
                 var categories = "<categories>";
                 var dataset1 = "<dataset seriesName='<fmt:message key='aimir.success'/>'>";
                 var dataset2 = "<dataset seriesName='<fmt:message key='aimir.fail'/>'>";
                 var dataset3 = "<dataset seriesName='<fmt:message key='aimir.invalidArgument'/>'>";
                 var dataset4 = "<dataset seriesName='<fmt:message key='aimir.commFailure'/>'>";

                 for(var index = 0  ;index<chartData.length;index++) {
                    if(index != "indexOf") {
                        categories += "<category label='"+chartData[index].xTag +"' />";
    
                        if(chartData[index].result1 > 0) dataset1 += "<set value='"+chartData[index].result1+"' />";
                        else dataset1 += "<set value='' />";
    
                        if(chartData[index].result2 > 0) dataset2 += "<set value='"+chartData[index].result2+"' />";
                        else dataset2 += "<set value='' />";
    
                        if(chartData[index].result3 > 0) dataset3 += "<set value='"+chartData[index].result3+"' />";
                        else dataset3 += "<set value='' />";
    
                        if(chartData[index].result4 > 0) dataset4 += "<set value='"+chartData[index].result4+"' />";
                        else dataset4 += "<set value='' />";
                    }
                 }

                categories += "</categories>";
                dataset1 += "</dataset>";
                dataset2 += "</dataset>";
                dataset3 += "</dataset>";
                dataset4 += "</dataset>";                                       
                                     
                meterTimeSyncLogRightChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";
                
                fcRightChartRenderer();

            }
        );
    }

    function updateMeterTimeSyncAuto() {

        
        $.getJSON('${ctx}/gadget/device/getMeterTimeSyncLogChart.do'
                ,{mcuSysId:$('#timeSyncLogMcuSysId').val(),
                    meterMdsId:$('#timeSyncLogMeterMdsId').val(),
                    operatorId:$('#timeSyncLogOperatorId').val(),
                    method:"Auto",
                    status:$('#timeSyncLogStatus').val(),
                    timeDiff:$('#timeSyncLogTimeDiff').val(),
                    time:$('#timeSyncLogTime').val(),
                    timeType:$('#timeSyncLogTimeType').val(),
                    searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val(),
                    supplierId:supplierId}
            ,function(json) {
                var chartData = json.chartData;
                meterTimeSyncLogLeftChartDataXml = "<chart "
                        + "yAxisName='<fmt:message key="aimir.count"/>' "
                        + "chartLeftMargin='0' "
                        + "chartRightMargin='0' "
                        + "chartTopMargin='10' "
                        + "chartBottomMargin='0' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='1' "
                        + "labelDisplay = 'rotate' "
                        + "slantLabels='1' "
                        + "labelStep='"+parseInt(chartData.length / 7, 10)+"' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_MSColumn3D_nobg 
                        + ">";
                 var categories = "<categories>";
                 var dataset1 = "<dataset seriesName='<fmt:message key='aimir.success'/>'>";
                 var dataset2 = "<dataset seriesName='<fmt:message key='aimir.fail'/>'>";
                 var dataset3 = "<dataset seriesName='<fmt:message key='aimir.invalidArgument'/>'>";
                 var dataset4 = "<dataset seriesName='<fmt:message key='aimir.commFailure'/>'>";

                 for(var index = 0  ;index<chartData.length;index++) {
                     if(index != "indexOf") {
                        categories += "<category label='"+chartData[index].xTag +"' />";
    
                        if(chartData[index].result1 > 0) dataset1 += "<set value='"+chartData[index].result1+"' />";
                        else dataset1 += "<set value='' />";
    
                        if(chartData[index].result2 > 0) dataset2 += "<set value='"+chartData[index].result2+"' />";
                        else dataset2 += "<set value='' />";
    
                        if(chartData[index].result3 > 0) dataset3 += "<set value='"+chartData[index].result3+"' />";
                        else dataset3 += "<set value='' />";
    
                        if(chartData[index].result4 > 0) dataset4 += "<set value='"+chartData[index].result4+"' />";
                        else dataset4 += "<set value='' />";
                     }
                 }

                categories += "</categories>";
                dataset1 += "</dataset>";
                dataset2 += "</dataset>";
                dataset3 += "</dataset>";
                dataset4 += "</dataset>";                                       
                                     
                meterTimeSyncLogLeftChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";

                fcLeftChartRenderer();
            }
        );

        $.getJSON('${ctx}/gadget/device/getMeterTimeSyncLogAutoChart.do'
                ,{mcuSysId:$('#timeSyncLogMcuSysId').val(),
                    meterMdsId:$('#timeSyncLogMeterMdsId').val(),
                    operatorId:$('#timeSyncLogOperatorId').val(),
                    method:"Auto",
                    status:$('#timeSyncLogStatus').val(),
                    timeDiff:$('#timeSyncLogTimeDiff').val(),
                    time:$('#timeSyncLogTime').val(),
                    timeType:$('#timeSyncLogTimeType').val(),
                    searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val(),
                    supplierId:supplierId}
            ,function(json) {
                var chartData = json.chartData;
                meterTimeSyncLogRightChartDataXml = "<chart "
                        + "yAxisName='<fmt:message key="aimir.count"/>' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='1' "
                        + "labelDisplay = 'rotate' "
                        + "slantLabels='1' "
                        + "labelStep='"+parseInt(chartData.length / 7, 10)+"' "
                        + "showYAxisValues='0' "
                         + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_MSColumn3D 
                        + ">";
                 var categories = "<categories>";
                 var dataset2 = "<dataset seriesName='<fmt:message key='aimir.fail'/>'>";
                 var dataset3 = "<dataset seriesName='<fmt:message key='aimir.invalidArgument'/>'>";
                 var dataset4 = "<dataset seriesName='<fmt:message key='aimir.commFailure'/>'>";

                 for(var index = 0  ;index<chartData.length;index++) {
                    if(index != "indexOf") {
                        categories += "<category label='"+chartData[index].xTag +"' />";
    
                        if(chartData[index].result2 > 0) dataset2 += "<set value='"+chartData[index].result2+"' />";
                        else dataset2 += "<set value='' />";
    
                        if(chartData[index].result3 > 0) dataset3 += "<set value='"+chartData[index].result3+"' />";
                        else dataset3 += "<set value='' />";
    
                        if(chartData[index].result4 > 0) dataset4 += "<set value='"+chartData[index].result4+"' />";
                        else dataset4 += "<set value='' />";
                    }
                 }

                 if(chartData.length == 0) {
                    categories += "<category label=' ' />";

                    dataset2 += "<set value='' />";
                    dataset3 += "<set value='' />";
                    dataset4 += "<set value='' />";
                 }

                categories += "</categories>";
                dataset2 += "</dataset>";
                dataset3 += "</dataset>";
                dataset4 += "</dataset>";                                       
                                     
                meterTimeSyncLogRightChartDataXml += categories + dataset2 + dataset3 + dataset4 + "</chart>";

                fcRightChartRenderer();

            }
        );
    }

    function updateMeterTimeSyncManual() {

        
        $.getJSON('${ctx}/gadget/device/getMeterTimeSyncLogChart.do'
                ,{mcuSysId:$('#timeSyncLogMcuSysId').val(),
                    meterMdsId:$('#timeSyncLogMeterMdsId').val(),
                    operatorId:$('#timeSyncLogOperatorId').val(),
                    method:"Manual",
                    status:$('#timeSyncLogStatus').val(),
                    timeDiff:$('#timeSyncLogTimeDiff').val(),
                    time:$('#timeSyncLogTime').val(),
                    timeType:$('#timeSyncLogTimeType').val(),
                    searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val(),
                    supplierId:supplierId}
            ,function(json) {
                var chartData = json.chartData;
                meterTimeSyncLogLeftChartDataXml = "<chart "
                        + "yAxisName='<fmt:message key="aimir.count"/>' "
                        + "showValues='0' "
                        + "showLabels='1' "
                        + "showLegend='1' "
                        + "labelDisplay = 'rotate' "
                        + "slantLabels='1' "
                        + "labelStep='"+parseInt(chartData.length / 7, 10)+"' "
                        + fChartStyle_Common
                        + fChartStyle_Font
                        + fChartStyle_MSColumn3D 
                        + ">";
                 var categories = "<categories>";
                 var dataset1 = "<dataset seriesName='<fmt:message key='aimir.success'/>'>";
                 var dataset2 = "<dataset seriesName='<fmt:message key='aimir.fail'/>'>";
                 var dataset3 = "<dataset seriesName='<fmt:message key='aimir.invalidArgument'/>'>";
                 var dataset4 = "<dataset seriesName='<fmt:message key='aimir.commFailure'/>'>";

                 for(var index = 0  ;index<chartData.length;index++) {
                    if(index != "indexOf") {
                        categories += "<category label='"+chartData[index].xTag +"' />";
    
                        if(chartData[index].result1 > 0) dataset1 += "<set value='"+chartData[index].result1+"' />";
                        else dataset1 += "<set value='' />";
    
                        if(chartData[index].result2 > 0) dataset2 += "<set value='"+chartData[index].result2+"' />";
                        else dataset2 += "<set value='' />";
    
                        if(chartData[index].result3 > 0) dataset3 += "<set value='"+chartData[index].result3+"' />";
                        else dataset3 += "<set value='' />";
    
                        if(chartData[index].result4 > 0) dataset4 += "<set value='"+chartData[index].result4+"' />";
                        else dataset4 += "<set value='' />";
                    }
                 }

                categories += "</categories>";
                dataset1 += "</dataset>";
                dataset2 += "</dataset>";
                dataset3 += "</dataset>";
                dataset4 += "</dataset>";
                                     
                meterTimeSyncLogLeftChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";

                fcLeftChartRenderer();
            }
        );

        $.getJSON('${ctx}/gadget/device/getMeterTimeSyncLogAutoChart.do'
                ,{mcuSysId:$('#timeSyncLogMcuSysId').val(),
                    meterMdsId:$('#timeSyncLogMeterMdsId').val(),
                    operatorId:$('#timeSyncLogOperatorId').val(),
                    method:"Manual",
                    status:$('#timeSyncLogStatus').val(),
                    timeDiff:$('#timeSyncLogTimeDiff').val(),
                    time:$('#timeSyncLogTime').val(),
                    timeType:$('#timeSyncLogTimeType').val(),
                    searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val(),
                    supplierId:supplierId}
            ,function(json) {
                var chartData = json.chartData;
                meterTimeSyncLogRightChartDataXml = "<chart "
                     + "yAxisName='<fmt:message key="aimir.count"/>' "
                     + "showZeroPies='1' "
                     + "showLabels='0' "
                     + "showValues='0' "
                     + "showLegend='1' "
                     + "showPercentInToolTip='0' "
                     + "legendPosition='RIGHT' "
                      + "manageLabelOverflow='1' "
                     + fChartStyle_Common
                     + fChartStyle_Font
                     + fChartStyle_Pie3D 
                     + ">";
                var labels = "";
                
                if(chartData.RESULT1 == null && chartData.RESULT2 == null && chartData.RESULT3 == null && chartData.RESULT4 == null) {
                     labels = "<set label='' value='1' color='E9E9E9' toolText='' />"
                } else {
                    if(chartData.RESULT1 != null) labels += "<set label='<fmt:message key='aimir.success'/>' value='"+chartData.RESULT1+"' />";
                    else labels += "<set label='<fmt:message key='aimir.success'/>' value='0' />";

                    if(chartData.RESULT2 != null) labels += "<set label='<fmt:message key='aimir.success'/>' value='"+chartData.RESULT2+"' />";
                    else labels += "<set label='<fmt:message key='aimir.success'/>' value='0' />";

                    if(chartData.RESULT3 != null) labels += "<set label='<fmt:message key='aimir.success'/>' value='"+chartData.RESULT3+"' />";
                    else labels += "<set label='<fmt:message key='aimir.success'/>' value='0' />";

                    if(chartData.RESULT4 != null) labels += "<set label='<fmt:message key='aimir.success'/>' value='"+chartData.RESULT4+"' />";
                    else labels += "<set label='<fmt:message key='aimir.success'/>' value='0' />";
                }
                                 
                meterTimeSyncLogRightChartDataXml += labels + "</chart>";

                fcRightChartRenderer();

            }
        );
    }

    function fcLeftChartRenderer() {
		console.log("fcLeftChartRenderer",meterTimeSyncLogLeftChartDataXml);
        var chartWidthTab = $('#fcLeftChartDiv').width();
        if(chartWidthTab == 0) {
            if($('#timeDiff').width() > 0)
                chartWidthTab = ($('#timeDiff').width() / 100) * 45;
            else if($('#timeThreshold').width() > 0)
                chartWidthTab = ($('#timeThreshold').width() / 100) * 45;
        }
/*         meterTimeSyncLogLeftChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcLeftChartId", chartWidthTab, 390, "0", "0");
        meterTimeSyncLogLeftChart.setDataXML(meterTimeSyncLogLeftChartDataXml);
        meterTimeSyncLogLeftChart.setTransparent("transparent");
        meterTimeSyncLogLeftChart.render("fcLeftChartDiv"); */
        meterTimeSyncLogLeftChart = new FusionCharts({
			type: 'StackedColumn3D',
			renderAt : 'fcLeftChartDiv',
			width : chartWidthTab,
			height : '390',
			dataSource : meterTimeSyncLogLeftChartDataXml
		}).render();
    }

    function fcRightChartRenderer() {
    	console.log("fcRightChartRenderer",meterTimeSyncLogRightChartDataXml);
        var chartWidthTab = $('#fcLeftChartDiv').width();
        if(chartWidthTab == 0) {
            if($('#timeDiff').width() > 0)
                chartWidthTab = ($('#timeDiff').width() / 100) * 45;
            else if($('#timeThreshold').width() > 0)
                chartWidthTab = ($('#timeThreshold').width() / 100) * 45;
        }

        var meterTimeSyncLogMethod = $('#timeSyncLogMethodView').val();
        if(meterTimeSyncLogMethod == "All") {
        	meterTimeSyncLogRightChart = new FusionCharts({
    			type: 'StackedColumn3D',
    			renderAt : 'fcRightChartDiv',
    			width : chartWidthTab,
    			height : '390',
    			dataSource : meterTimeSyncLogRightChartDataXml
    		}).render();
        	
            /* meterTimeSyncLogRightChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcRightChartId", chartWidthTab, 390, "0", "0");
            meterTimeSyncLogRightChart.setDataXML(meterTimeSyncLogRightChartDataXml);
            meterTimeSyncLogRightChart.setTransparent("transparent");
            meterTimeSyncLogRightChart.render("fcRightChartDiv"); */
        } else if(meterTimeSyncLogMethod == "Auto") {
        	meterTimeSyncLogRightChart = new FusionCharts({
    			type: 'StackedBar3D',
    			renderAt : 'fcRightChartDiv',
    			width : chartWidthTab,
    			height : '390',
    			dataSource : meterTimeSyncLogRightChartDataXml
    		}).render();
        	/* 
            meterTimeSyncLogRightChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedBar3D.swf", "fcRightChartId", chartWidthTab, 390, "0", "0");
            meterTimeSyncLogRightChart.setDataXML(meterTimeSyncLogRightChartDataXml);
            meterTimeSyncLogRightChart.setTransparent("transparent");
            meterTimeSyncLogRightChart.render("fcRightChartDiv"); */
        } else if(meterTimeSyncLogMethod == "Manual") {
        	meterTimeSyncLogRightChart = new FusionCharts({
    			type: 'Pie3D',
    			renderAt : 'fcRightChartDiv',
    			width : chartWidthTab,
    			height : '390',
    			dataSource : meterTimeSyncLogRightChartDataXml
    		}).render();
        	
            /* meterTimeSyncLogRightChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "fcRightChartId", chartWidthTab, 390, "0", "0");
            meterTimeSyncLogRightChart.setDataXML(meterTimeSyncLogRightChartDataXml);
            meterTimeSyncLogRightChart.setTransparent("transparent");
            meterTimeSyncLogRightChart.render("fcRightChartDiv"); */
        }
    }
    //TimeSyncLog 엑셀 
    var winTimeSyncLog;
    function exprortExcelTimeSyncLogGrid(){
        var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage1 = new Array();
        var condition1  = new Array();

        fmtMessage1 = getMsgTimeSyncLogGrid();
        condition1 = getConditionTimeSyncLog();

        obj.condition = condition1;
        obj.fmtMessage = fmtMessage1;
        obj.url = '${ctx}/gadget/device/meterTimeSyncMaxExcelMake.do';

        if(winTimeSyncLog)
            winTimeSyncLog.close();
        winTimeSyncLog = window.open("${ctx}/gadget/ExcelDownloadPopup.do","MeterTimeSyncLogExcel", opts);
        winTimeSyncLog.opener.obj = obj;
    }
    
    function timeSyncLogType() {
        if($('#timeSyncLogTimeDiff').val() == 6) {
            $('#timeSyncLogSecSearch').show();
            $('#timeSyncLogSecSearchFmt').show();
        } else {
            $('#timeSyncLogTime').val("");
            $('#timeSyncLogSecSearch').hide();
            $('#timeSyncLogSecSearchFmt').hide();
        }
        
    }

    //################### 2. Meter Time Sync Log 탭 End ###################### 

    //################### 3. DCU Meter Time Threshold Setting 탭 ###################### 
    //TimeThreadhold 전체 조회
    /* function thresholdSearch(){
        getMeterTimeThresholdGrid();
    } */
    //TimeThreadhold 메시지
/*    function getMsgThreadholdGrid(){

         var fmtMessage = new Array();
         fmtMessage[0] = "";
         fmtMessage[1] = "<fmt:message key="aimir.orderNo"/>";
         fmtMessage[2] = "<fmt:message key="aimir.mcuid"/>";
         fmtMessage[3] = "<fmt:message key="aimir.mcutype"/>";
         fmtMessage[4] = "<fmt:message key="aimir.mcu.name"/>";
         fmtMessage[5] = "<fmt:message key="aimir.mcumobile"/>";
         fmtMessage[6] = "<fmt:message key="aimir.ipaddress"/>";
         fmtMessage[7] = "<fmt:message key="aimir.threshold"/>";
         fmtMessage[8] = "<fmt:message key="aimir.lastcomm"/>";
         fmtMessage[9] = "<fmt:message key="aimir.commstate"/>";         
         fmtMessage[10] = "<fmt:message key="aimir.excel.meterTimeThreshold"/>";
         fmtMessage[11] = "<fmt:message key="aimir.normal"/>";
         fmtMessage[12] = "<fmt:message key="aimir.commstateYellow"/>";
         fmtMessage[13] = "<fmt:message key="aimir.commstateRed"/>";
         return fmtMessage;
    }

    //TimeThreadhold 조건
    function getConditionThreshold(){
        var condArray = new Array();

        condArray[0] = $('#timeThresHoldMcuSysType').val();
        condArray[1] = $('#timeThresHoldMcuSysId').val();
        condArray[2] = $('#timeThresHoldMcuCommState').val();

        condArray[3] = $('#timeThresHoldLocationId').val();
        condArray[4] = $('#timeThresHoldTime').val();
        condArray[5] = $('#timeThresHoldTimeDiffType').val();

        condArray[6] = supplierId;

        return condArray;
    }
    //TimeThreadhold 그리드
    var meterTimeThresholdGridStore;
    var meterTimeThresholdGridColModel;
    var meterTimeThresholdGridOn = false;
    var meterTimeThresholdGrid;
    var meterTimeThresholdCheckSelModel;
    function getMeterTimeThresholdGrid(){
        var arrayObj = getConditionThreshold();
        var fmtMessage = getMsgThreadholdGrid();
        var width = $("#meterTimeThresholdGridDiv").width(); 

         meterTimeThresholdGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/device/getMeterTimeThresholdGrid.do",
            baseParams:{
                mcuSysType    : arrayObj[0],
                mcuSysId      : arrayObj[1],
                mcuCommState  : arrayObj[2],
                locationId    : arrayObj[3],
                time          : arrayObj[4],
                timeDiffType  : arrayObj[5],
                supplierId    : arrayObj[6],
                pageSize      : 10
            },
            totalProperty: 'totalCnt',
            root:'gridData',
             fields: [
            { name: 'no'        , type: 'Integer' },
            { name: 'sysId'     , type: 'String' },
            { name: 'sysType'   , type: 'String' },
            { name: 'sysName'   , type: 'String' },
            { name: 'phone'     , type: 'String' },
            { name: 'ipAddr'    , type: 'String' },
            { name: 'threshold' , type: 'String' },
            { name: 'lastComm'  , type: 'String' },
            { name: 'commState' , type: 'String' }
            ],
            listeners: {
            beforeload: function(store, options){
            options.params || (options.params = {});
            Ext.apply(options.params, {
                          curPage: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                     });
            }
        }
        });
        
        if(meterTimeThresholdGridOn == false) {
                meterTimeThresholdCheckSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly:true
                });
            }

        meterTimeThresholdGridColModel = new Ext.grid.ColumnModel({
            columns: [
            meterTimeThresholdCheckSelModel,
                {header: fmtMessage[1], 
                   dataIndex: 'no', 
                   align:'center', 
                   width: width/9-20
                 }
                 ,{header: fmtMessage[2], 
                   dataIndex: 'sysId', 
                   align:'center', 
                   width: width/9
                 }
                ,{header: fmtMessage[3],
                    width: width/9, 
                    dataIndex: 'sysType', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[4],
                    width: width/9, 
                    dataIndex: 'sysName', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[5],
                    width: width/9, 
                    dataIndex: 'phone', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[6],
                    width: width/9, 
                    dataIndex: 'ipAddr', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[7],
                    width: width/9, 
                    dataIndex: 'threshold', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[8],
                    width: width/9+20, 
                    dataIndex: 'lastComm', 
                    align: 'center'
                 }
                 ,{header: fmtMessage[9],
                    width: width/9, 
                    dataIndex: 'commState', 
                    align: 'center',
                    renderer: commStateResult
                 }
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                ,width: ((width-10)/9)-chromeColAdd

            },

        });
        if (meterTimeThresholdGridOn == false) {
            meterTimeThresholdGrid = new Ext.grid.GridPanel({
                id: 'meterTimeThresholdGrid',
                store: meterTimeThresholdGridStore,
                colModel : meterTimeThresholdGridColModel,
                sm: meterTimeThresholdCheckSelModel,
                autoScroll: false,
                width: width,
                height: 290,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'meterTimeThresholdGridDiv',
                viewConfig: {
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: meterTimeThresholdGridStore,
                    displayInfo: true,

                    displayMsg: ' {0} - {1} / {2}'                
                })
            });
            meterTimeThresholdGridOn  = true;
        } else {
            
            meterTimeThresholdGrid.setWidth(width);
            meterTimeThresholdGrid.reconfigure(meterTimeThresholdGridStore, meterTimeThresholdGridColModel);
            var bottomToolbar = meterTimeThresholdGrid.getBottomToolbar();                        
            bottomToolbar.bindStore(meterTimeThresholdGridStore);
        }

    }

     //TimeThreadhold commState 상태 renderer
    function commStateResult(value, metadata, record, rowIndex, colIndex, store){

        var commstate = record.json.commState;
        var fmtMessageCommAlert = getMsgThreadholdGrid(); 
        if(commstate == "0"){
            return fmtMessageCommAlert[11];
        }else if(commstate == "24"){
            return fmtMessageCommAlert[12];
        }else if(commstate == "48"){
            return fmtMessageCommAlert[13];
        }
    };
    //TimeThreadhold 설정 버튼 이벤트
    var mcuList = new Array();
     function thresholdSet(){

        var checkedArr = meterTimeThresholdCheckSelModel.getSelections();

        if (checkedArr.length > 0) {
                for (var i = 0 ; i < checkedArr.length ; i++) {
                    if (checkedArr[i].get("sysId") != null && checkedArr[i].get("sysId") != "") {
                        mcuList.push(checkedArr[i].get("sysId"));
                    }
                }
        }
        Ext.getCmp('setting_threshold_panel').getForm().reset();
        sForm.setTitle('<fmt:message key="aimir.setting"/>');
        if(mcuList.length == 0){
            Ext.Msg.alert("", "<fmt:message key="aimir.notSelectMcu"/>");
            return;
        }else{
            sForm.show();
        }
    }

     function getPopMsgThreadhold(){

         var dataGrid = new Array();
         dataGrid[0] = "<fmt:message key="aimir.thresholdInsert"/>";    // 설정할 Threshold값을 입력하세요
         dataGrid[1] = "<fmt:message key="aimir.thresholding"/>";       // Threshold값을 설정중입니다.

         return dataGrid;
     }

     //TimeThreadhold 설정 폼 
     var renderSettingThresholdForm = function(params) {
                
         var msg = getPopMsgThreadhold();
         var formItem = [ {
                xtype:'textfield',
                hiddenName:'threshold',
                fieldLabel: msg[0],
                name:'threshold'
         }];
        
        var settingThresholdForm = Ext.apply({}, BASE.form.frame);
        settingThresholdForm.id = 'setting_threshold_panel',
        
        settingThresholdForm.buttons[0].handler = params.handler.saveHandler;
        settingThresholdForm.buttons[1].handler = params.handler.cancelHandler;
        
        var len = formItem.length;
        while(len--) {
          formItem[len] = Ext.apply(formItem[len], BASE.form[formItem[len].xtype]);
        };
        settingThresholdForm.items = formItem;
        var window = Ext.apply({}, BASE.window);
        window.id = 'threshold_form';
        window.items = new Ext.FormPanel(settingThresholdForm);
        
        var sf = new Ext.Window(window);
          sf.render('meterTime_threshold_form');
          sf.setTitle('<fmt:message key="aimir.setting"/>');
          
          if(renderObjects) {
            renderObjects[window.id] = sf;
          }
          
          return sf;
    };

    function SettingThresholdForm(){

        var f = Ext.getCmp('setting_threshold_panel').getForm();
        var p = f.getValues();

        if(p.threshold == null || p.threshold == ""){
            Ext.Msg.alert("<fmt:message key="aimir.error"/>", "<fmt:message key="aimir.thresholdInsert"/>");
            return;
        }else{

            var params = {
                            threshold:p.threshold,
                            mcuList:mcuList.toString()
                        };

            $.post("${ctx}/gadget/device/setMeterTimeThreshold.do",
                   params,
                   function(json) {
                       if (json != null && json.result == "success") {
                           var resultmsg = json.rtnStr;
                           Ext.Msg.alert("",
                            resultmsg + " "+'<fmt:message key="aimir.mcu.setmetertimethreshold"/>',
                                   function() {
                                    meterTimeThresholdGridStore.reload();
                                    meterTimeThresholdGridStore.rejectChanges();
                                    sForm.hide();
                                    setMsgThreadholdResult(rtnStr);
                                   });
                       } else {
                           Ext.Msg.alert("<fmt:message key="aimir.error"/>", "<fmt:message key="aimir.error"/>");
                       }

                       return;
                   }
            );
        }
    };
    //TimeThreadhold 설정 성공시 결과값 
     function setMsgThreadholdResult(rtnStr){

         var tempStr = "<fmt:message key="aimir.success.count"/>" +" : "+ rtnStr;
         
         $('#thresholdResult').val(tempStr);
        
     }
     //TimeThreadhold 엑셀 
     function exprortExcelTimeThresholdGrid(){
        var opts = "dialogWidth:400px; dialogHeight:330px;resizable:no;status:no;help:no;center:yes;";
        var obj = new Object();
        var fmtMessage1 = new Array();
        var condition1  = new Array();

        fmtMessage1 = getMsgThreadholdGrid();
        condition1 = getConditionThreshold();

        obj.condition = condition1;
        obj.fmtMessage = fmtMessage1;
        obj.url = '${ctx}/gadget/device/meterTimeThresholdMaxExcelMake.do';
        var win = window
                .showModalDialog(
                        "${ctx}/gadget/ExcelDownloadPopup.do",
                        obj, opts);
    } */

    //################### 3. DCU Meter Time Threshold Setting 탭 End ##################
    /*]]>*/
    </script>
</head>

<body>

<!-- 검색옵션배경 (S) -->
    <div id="meterTimeMax">
        <ul>
            <li><a href="#timeDiff"      id="_timeDiff"      ><fmt:message key="aimir.view.metertimecheck"/></a></li>
          	<li><a href="#timeSyncLog"   id="_timeSyncLog"   ><fmt:message key="aimir.view.metertimesynclog"/></a></li>
          	<%-- <li><a href="#timeThreshold" id="_timeThreshold" ><fmt:message key="aimir.timethresholdmculist"/></a></li> --%>
        </ul>

       <!-- 1ST 탭 전체 : Time Diff (S) -->
        <div id="timeDiff">

			<!-- 1ST 탭 : 검색조건 (S) -->
			<div class="searchbox padding-b3px">
				<table class="searching">
					<tr>
						<td class="withinput"><fmt:message key="aimir.mcuid"/> </td>
						<td class="padding-r20px"><input type="text" id="timeDiffMcuSysId"/></td>
						<td class="withinput"><fmt:message key="aimir.customername"/> </td>
						<td class="padding-r20px"><input type="text" id="timeDiffCustomerName" style="width:130px;"/></td>
						<td class="withinput"><fmt:message key="aimir.meterid"/> </td>
						<td class="padding-r20px"><input type="text" id="timeDiffMeterMdsId" style="width:130px;"/></td>
					</tr>

					<tr>
						<td class="withinput"><fmt:message key="aimir.contractNumber"/> </td>
						<td class="padding-r20px"><input type="text" id="timeDiffContractNumber"/></td>
                        <td class="withinput"><fmt:message key="aimir.compliace"/></td>
                        <td>
                            <select id="timeDiffCompliance" style="width:130px;">
                                <option value=""><fmt:message key="aimir.all"/></option>
                                <option value="Y"><fmt:message key="aimir.yes"/></option>
                                <option value="N"><fmt:message key="aimir.no"/></option>
                            </select>
                        </td>
						<td class="withinput"><fmt:message key="aimir.timediff"/></td>
						<td class="padding-r20px">
							<span>
								<select id="timeDiffTimeDiff" style="width:131px;" onchange="timeDiffType()">
									<option value=""><fmt:message key="aimir.all"/></option>
									<option value="1"><fmt:message key="aimir.normal"/></option>
									<option value="5"><fmt:message key="aimir.bems.facilityMgmt.unknown"/></option>
									<option value="2">1~12 <fmt:message key="aimir.hour"/></option>
									<option value="3">12~24 <fmt:message key="aimir.hour"/></option>
									<option value="4">24 <fmt:message key="aimir.hour"/> <fmt:message key="aimir.over"/></option>
                                    <option value="6"><fmt:message key="dashboard.userdefine"/></option>
								</select>
							</span>
						</td>
                        <td id="timeDiffSecSearch" style="display: none;"><input type="text" id="timeDiffTime"/></td>
                        <td class="withinput" id="timeDiffSecSearchFmt" style="display: none;"><fmt:message key="aimr.morethan"/>(<fmt:message key="aimir.sec"/>)</td>
						<td>
							<em class="am_button"><a href="javascript:timeDiffSearch()" class="on"><fmt:message key="aimir.button.search" /></a></em>
						</td>
					</tr>
				</table>
			</div>
			<!-- 1ST 탭 : 검색조건 (E) -->




			<!-- 1ST 탭 : 그리드 (S) -->
			
			<div  id="gadget_body clear">
			
			<div class="width-auto textalign-center">
				<ul>
					<li class="display_inline" id="fcMeterTimeDiffChart">The chart will appear within this DIV. This text will be replaced by the chart.</li>
					<li class="display_inline" id="fcMeterTimeDiffComplianceChart">The chart will appear within this DIV. This text will be replaced by the chart.</li>
				</ul>
			</div>
            <div id="drAlertTimeSyncWin"></div>
	
			</div>
			<div class="clear"></div>
			<div id="btn" class="btn_right_top2 margin-t10px">
				<ul id="timeSyncBtn"><li><a href="javaScript:groupTimeDiffSync();" class="on">TimeSync</a></li></ul>
				<ul><li><a href="javaScript:exportExcelMeterTimeDiffGrid();" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
			</div>
			<div class="gadget_body2">
				<div id="meterTimeDiffGridDiv"></div>
			</div>
			<!-- 1ST 탭 : 그리드 (E) -->

		</div>
        <!-- 1ST 탭 전체 : Time Diff (E) -->
		
		<!-- 2ND 탭 전체 : TimeSyncLog (S) -->
        <div id="timeSyncLog">

			<!-- 2ND 탭 : 검색조건 (S) -->
			<div class="searchbox">

				<div class="dayoptions-bt">
				<%@ include file="/gadget/commonDateTabButtonType.jsp"%>
				</div>
				<div class="dashedline"></div>

				<div class="margin-l10">
					<table class="searching">
						<tr><td class="withinput"><fmt:message key="aimir.mcuid"/></td>
							<td class="padding-r20px"><input type="text" id="timeSyncLogMcuSysId" style="width:130px;"/></td>
							<td class="withinput"><fmt:message key="aimir.meterid"/> </td>
							<td class="padding-r20px"><input type="text" id="timeSyncLogMeterMdsId" style="width:130px;"/></td>
							<td class="withinput"><fmt:message key="aimir.operator"/></td>
							<td><input type="text" id="timeSyncLogOperatorId" style="width:130px;"/></td>
						</tr>

						<tr>
							<td class="withinput"><fmt:message key="aimir.auto"/>/<fmt:message key="aimir.manual"/></td>
							<td class="padding-r20px">
								<select id="timeSyncLogMethodView" style="width:130px;">
									<option value="All"><fmt:message key="aimir.all"/></option>
									<option value="Auto"><fmt:message key="aimir.auto"/></option>
									<option value="Manual"><fmt:message key="aimir.manual"/></option>
								</select>
								<input type="hidden" id="timeSyncLogMethod" value="All"/>
							</td>
							<td class="withinput"><fmt:message key="aimir.status"/></td>
							<td class="padding-r20px">
								<select id="timeSyncLogStatus" style="width:130px;">
									<option value=""><fmt:message key="aimir.all"/></option>
									<option value="Success"><fmt:message key="aimir.success"/></option>
									<option value="Fail"><fmt:message key="aimir.fail"/></option>
								</select>
							</td>
							<td class="withinput"><fmt:message key="aimir.timediff"/></td>
							<td>
								<span>
									<select id="timeSyncLogTimeDiff" style="width:130px;" onchange="timeSyncLogType();">
										<option value=""><fmt:message key="aimir.all"/></option>
										<option value="1"><fmt:message key="aimir.normal"/></option>
										<option value="5"><fmt:message key="aimir.bems.facilityMgmt.unknown"/></option>
										<option value="2">1~12 <fmt:message key="aimir.hour"/></option>
										<option value="3">12~24 <fmt:message key="aimir.hour"/></option>
										<option value="4">24 <fmt:message key="aimir.hour"/> <fmt:message key="aimir.over"/></option>
                                        <option value="6"><fmt:message key="dashboard.userdefine"/></option>
									</select>
								</span>
							</td>
                            <td id="timeSyncLogSecSearch" style="display: none;"><input type="text" id="timeSyncLogTime"/></td>
                            <td class="withinput" id="timeSyncLogSecSearchFmt" style="display: none;"><fmt:message key="aimr.morethan"/>(<fmt:message key="aimir.sec"/>)</td>
							<td>
								<em class="am_button"><a href="javascript:timeSyncLogSearch();" class="on"><fmt:message key="aimir.button.search" /></a></em>
							</td>
						</tr>
					</table>
				</div>
			</div>
			<!-- 2ND 탭 : 검색조건 (E) -->

			<!-- 2ND 탭 : 그리드 (S) -->
			<div id="gadget_body clear">
				<div id="tab2ChartSpace" class="w_auto overflow_hidden padding10px">
					
				  <!--항상나옴-->
					<div class="floatleft width-49 padding-t10px">
						<label><input type="text" id="methodLeftHead" value="Method : Manual" class="border-trans bold padding-l20px"/></label>
						<div id="fcLeftChartDiv" class="w_auto margin20px">
							The chart will appear within this DIV. This text will be replaced by the chart.
						</div>
					</div>

					<!--교체-->
					<div class="floatright width-49 padding-t10px">
						<label><input type="text" id="methodRightHead" value="Method : Manual" class="border-trans bold padding-l20px"/></label>
						<div id="fcRightChartDiv" class="w_auto margin20px">
							The chart will appear within this DIV. This text will be replaced by the chart.
						</div>
					</div>
				</div>
			</div>

			<div id="btn" class="btn_right_top2 margin-t10px">
				<ul><li><a href="javaScript:exprortExcelTimeSyncLogGrid();" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
			</div>
			
			<div class="gadget_body2">
                <div id="meterTimeSyncGridDiv"></div>
			</div>
			<!-- 2ND 탭 : 그리드 (E) -->

        </div>
        <!-- 2ND 탭 전체 : TimeSyncLog (E) -->
        
        
                <!-- 3RD 탭 전체 : Time Threshold(S) -->
<%--         <div id="timeThreshold" style="display: none">


			<!-- 3RD 탭 : 검색조건 (S) -->
			<div class="searchbox">

				
					<table class="searching">
					<tr><td class="withinput"><fmt:message key="aimir.mcutype"/></td>
						<td class="padding-r20px"><select id="timeThresHoldMcuSysType" style="width:142px"></select></td>
						<td class="withinput"><fmt:message key="aimir.mcuid"/> </td>
						<td class="padding-r20px"><input type="text" id="timeThresHoldMcuSysId" style="width:142px"/></td>
						<td class="withinput"><fmt:message key="aimir.commstatus"/></td>
						<td class="padding-r20px">
							<select id="timeThresHoldMcuCommState" style="width:170px;">
								<option value=""><fmt:message key="aimir.all"/></option>
								<option value="0"><fmt:message key="aimir.normal"/></option>
								<option value="24"><fmt:message key="aimir.commstateYellow"/></option>
								<option value="48"><fmt:message key="aimir.commstateRed"/></option>
							</select>
						</td>
					</tr>

					<tr>
						<td class="withinput"><fmt:message key="aimir.location"/></td>
						<td>
							<input type="text" id="timeThresHoldLocationIdText" name="location.name" style="width:142px">
				         	<input type="hidden" id="timeThresHoldLocationId" name="location.id" value="" />
							<div id="treeDivAOuter" class="tree-billing6" style="display:none;">
								<div id="treeDivA"></div>
							</div>
						</td>
						<td class="withinput"><fmt:message key="aimir.threshold"/></td>
						<td>
							<span><input type="text" id="timeThresHoldTime" style="width:93px;"/></span>
							<span>
								<select id="timeThresHoldTimeDiffType" style="width:50px;">
										<option value="sec"><fmt:message key="aimir.sec"/></option>
										<option value="min"><fmt:message key="aimir.minute"/></option>
										<option value="hour"><fmt:message key="aimir.hour"/></option>
								</select>
							</span>
							<span class="withinput"><fmt:message key="aimir.over"/></span>
						</td>
						<td colspan="2">
							<em class="am_button"><a href="javascript:thresholdSearch();"class="on"><fmt:message key="aimir.button.search" /></a></em>
						</td>
					</tr>
					</table>
				

			</div>
			<!-- 3RD 탭 : 검색조건 (E) -->


			<!-- 3RD 탭 : 그리드 (S) -->
			<div class="btn_right_top2 margin-t10px">
				<span id="selMcuLabel" class="withinput"><fmt:message key="aimir.selectMcu"/></span>
				<span id="thresholdBtn" class="btn_bluegreen"><a href="javascript:thresholdSet();" >Threshold</a></span>
				<span class="am_button margin-l5"><a href="javaScript:exprortExcelTimeThresholdGrid();" class="on"><fmt:message key="aimir.button.excel"/></a></span>
			</div>
			<div class="gadget_body2">
				<div class="flexlist">
					<div id="meterTimeThresholdGridDiv"></div>
                    <div id="meterTime_threshold_form"></div>
				</div>
				<div class="headspace_2ndline2"><label class="check"><fmt:message key="aimir.thresholdResult"/></label></div>
				<div class="metertime-textarea margin-t10px">
				   <ul><li><textarea id="thresholdResult" readonly></textarea></li></ul>
				</div>
			</div>
			<!-- 3RD 탭 : 그리드 (S) -->

		</div>
        <!-- 3RD 탭 전체 : Time Diff (E) --> --%>




    </div>
<!-- 검색옵션배경 (E) -->

</body>
</html>

