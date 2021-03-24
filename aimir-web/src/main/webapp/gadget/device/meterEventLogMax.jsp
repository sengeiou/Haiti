<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <title></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
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
        .x-grid3-row td.x-grid3-cell-meterEventName{
            padding-left: 30px !important;
            font-weight: bold !important;
        }
        #typeFChartDiv{
        	float: left;
        	overflow-y: auto;
        	overflow-x: hidden;
        	height:290px;
        	width: 100%;
        }
        #chartWrapper{
        	float: left;
        	height:330px;
        }
        #chartTitle{
        	height: 30px;
        	margin: auto;
        	vertical-align: middle;
    		font-weight: bold;
        }
        #gridTitle{
        	height: 30px;
        	margin: auto;
        	vertical-align: middle;
    		font-weight: bold;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //탭초기화
    var tabs = {hourly:0,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:1,yearly:0};
    var tabNames = {};

    var typeFChartDataXml;
    var typeFChart;

    var freqFChartDataXml;
    var freqFChart;

    var termFChartDataXml;
    var termFChart;
    var chromeColAdd = 2;
    var numberFormat = "${numberFormat}";

    var supplierId = "${supplierId}";
    var isEnableFreqGrid = false;
    var activatorId = "";
    var seleventName = "";
    // 수정권한
    var editAuth = "${editAuth}";
    var meterEventParams = Array();

    var supplier_Name;
   
    //fChart height 조절을 위한 변수
    var listLen = 0;

    $(document).ready(function() {
        Ext.QuickTips.init();
        Ext.Ajax.timeout = 300000;
        //pageInit();

        /**
         * 유저 세션 정보 가져오기
         */
        Ext.QuickTips.init();
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        if(json.supplierName == 'MOE'){
                            // 공급사가 이라크 MOE인 경우
                            supplier_Name = 'MOE';
                        }
                    }
                    pageInit();
                }
        );
    });

    $(window).resize(function() {
        fChartRender();

        if(!(meterEventLogMeterByEventGrid == undefined)){
            meterEventLogMeterByEventGrid.destroy();
        }
        meterEventLogMeterByEventGridOn = false;
        getMeterEventLogMeterByEventGrid();

        if(!(meterEventLogEventByMeterGrid == undefined)){
            meterEventLogEventByMeterGrid.destroy();
        }
        meterEventLogEventByMeterGridOn = false;
        getMeterEventLogEventByMeterGrid();

        if(!(meterEventLogProfileMaxGrid == undefined)){
            meterEventLogProfileMaxGrid.destroy();
        }
        meterEventLogProfileMaxGridOn = false;
        getMeterEventLogProfileMaxGrid();
    });

    function getEditAuth() {
        return editAuth;
    }

    //tab 클릭 이벤트
    function changeTab(tabId) {
        if (tabId == 'eventType') {
            if (!$('#typeDataDiv').is(':visible')) {
                $('#filterSetDiv').hide();
                $('#filterSetTab').removeClass('current');

                $('#typeSearch').show();
                $('#eventTypeTab').addClass('current');
                //$('#typeDataDiv').show();
                //searchList();
            }
        } else if(tabId == 'filterSet') {

            if(!$('#filterSetDiv').is(':visible')) {
                $('#typeSearch').hide();
                $('#typeDataDiv').hide();

                $('#filterSetDiv').show();

                $('#eventTypeTab').removeClass('current');
                $('#filterSetTab').addClass('current');

                getMeterEventLogProfileMaxGrid();
                if (getEditAuth() != "true") {
                    $('#applyBtn').hide();
                }
            }
        }
    }

    // Event Name combo 생성
    function makeEventNameSelectBox() {
        $.getJSON('${ctx}/gadget/device/getMeterEventLogComboData.do',
                function(json) {
                    var list = json.result;

                    if (list.length > 0) {
                        $('#eventName').loadSelect(list);
                        $("#eventName").val('');
                        $("#eventName").selectbox();
                    }
                }
        );
    }

    // Meter Type combo 생성
    function makeMeterTypeSelectBox() {
        var list = new Array();
        var obj = new Object();

        $.each(MeterType, function(key, value) {
            obj = new Object();
            obj.id = value;
            obj.name = value;
            list.push(obj);
        });
        list.sort(meterSort);

        if (list.length > 0) {
            $('#meterType').loadSelect(list);
            $("#meterType").val('');
            $("#meterType").selectbox();
        }
    }
    var meterSort = function(a,b) {
        if (a.name == b.name) {return 0}
        return a.name > b.name ? 1:-1;
    };

    // init
    function pageInit() {
        hide();
        makeEventNameSelectBox();
        makeMeterTypeSelectBox();
        locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
/*         updateTypeFChart(); // Chart 조회
        getMeterEventLogMeterByEventGrid();
        getMeterEventLogEventByMeterGrid(); */

    }

    //전체 조회
    function searchList() {
        activatorId = "";
        seleventName = $('#eventName').val();
        $('#typeDataDiv').show();
        updateTypeFChart();
        getMeterEventLogMeterByEventGrid();	
        getMeterEventLogEventByMeterGrid();
    }

    //event 별 그리드 조회
    function searchMeterByEventList(eventName) {
        activatorId = "";
        seleventName="";
        updateTypeFChart(eventName);
        seleventName = eventName;
        getMeterEventLogMeterByEventGrid();
        getMeterEventLogEventByMeterGrid();

    }

    //event 그리드 row 클릭 이벤트
    function searchEventByMeterList(eventName, meterId) {
        activatorId = meterId;
        seleventName = eventName;

        getMeterEventLogEventByMeterGrid();
    }
    
    // SP-1021 Search Tips (S)
    var imgWin;
    function openSearchTip() {
    	if(imgWin == null){
        	imgWin = new Ext.Window({
                title : 'Metering Data Gadget Search Tips',
                id : 'drAlertWinId',
                applyTo : 'serchTipWin',
                pageX : $('#searchTipDiv').offset().left,
                pageY : $('#searchTipDiv').offset().top,
                html: '<table id="searchTipTable"><tr><td>abc<b>%</b></td><td>Finds any values that start with \"abc\"</td></tr><tr><td><b>%</b>abc</td><td>Finds any values that end wiht \"abc\"</td></tr></table>',
                //boxMinWidth : 500,
                //height : 500,
                modal : false,
                resizable : false,
                closeAction : 'hide'
            });
    	}
        Ext.getCmp('drAlertWinId').show();
    }
    // SP-1021 Search Tips (E)

    //메시지 설정
    function getFmtMessage() {
        var fmtMessage = new Array();
        var idx = 0;

        fmtMessage[idx++] = "<fmt:message key="aimir.number"/>";                // 번호
        fmtMessage[idx++] = "<fmt:message key="aimir.opentime"/>";              // 발생시각
        fmtMessage[idx++] = "<fmt:message key="aimir.writetime"/>";             // 저장시각
        fmtMessage[idx++] = "<fmt:message key="aimir.device.eventName"/>";      // Event Name
        fmtMessage[idx++] = "<fmt:message key="aimir.location"/>";              // Location
        fmtMessage[idx++] = "<fmt:message key="aimir.meterid"/>";               // Meter Id
        fmtMessage[idx++] = "<fmt:message key="aimir.metertype"/>";             // Meter Type
        fmtMessage[idx++] = "<fmt:message key="aimir.message"/>";               // Message
        fmtMessage[idx++] = "<fmt:message key="aimir.device.troubleAdvice"/>";  // Trouble Advice
        fmtMessage[idx++] = "<fmt:message key="aimir.button.excel"/>";          // Excel
        fmtMessage[idx++] = "<fmt:message key="aimir.device.occurFreq"/>";      // 지속발생횟수
        fmtMessage[idx++] = "<fmt:message key="aimir.checkBox"/>";              // 체크
        fmtMessage[idx++] = "<fmt:message key="aimir.button.search"/>";         // 검색
        fmtMessage[idx++] = "<fmt:message key="aimir.button.apply"/>";          // 적용
        fmtMessage[idx++] = "<fmt:message key="aimir.save"/>";                  // 저장되었습니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.save.error"/>";            // 저장되지 않았습니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.device.max10"/>";          // ! 최대 10개만 설정 가능합니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.env.error"/>";             // 사용할 수 없는 환경입니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.firmware.msg09"/>";        // 데이터를 찾을수 없습니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.excel.metereventlog"/>";        // 데이터를 찾을수 없습니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.shipment.gs1"/>";          // GS1

        return fmtMessage;
    }

    // 타입별 조회시 사용될 검색조건
    var getConditionArray = function() {
        var arrayObj = Array();
        var idx = 0;

        arrayObj[idx++] = $('#searchStartDate').val();
        arrayObj[idx++] = $('#searchEndDate').val();
        arrayObj[idx++] = $('#searchDateType').val();
        arrayObj[idx++] = $('#locationId').val();
        arrayObj[idx++] = $('#eventName').val();
        arrayObj[idx++] = $('#meterType').val();
        arrayObj[idx++] = $('#meterId').val();
        arrayObj[idx++] = $('#occurFreq').val();
        arrayObj[idx++] = supplierId;
        arrayObj[idx++] = activatorId;
        arrayObj[idx++] = $('#gs1').val();
        return arrayObj;
    };
    
    // fchart 리스트 길이를 전역변수 listLen으로 넘겨준다
    function setListLen(len){
        listLen = len;
    }
    
    function updateTypeFChart(eventName) {
        emergePre();
        $.getJSON('${ctx}/gadget/device/getMeterEventLogMaxChartData.do'
                ,{searchStartDate : $('#searchStartDate').val(),
                    searchEndDate : $('#searchEndDate').val(),
                    searchDateType : $('#searchDateType').val(),
                    locationId : $('#locationId').val(),
                    eventName : seleventName,
                    meterType : $('#meterType').val(),
                    meterId : $('#meterId').val(),
                    gs1: $('#gs1').val(),                    
                    occurFreq : $('#occurFreq').val(),
                    supplierId : supplierId}
                ,function(json) {
                    var list = json.chartData;
                    var count = '<fmt:message key="aimir.count"/>';

                    if(eventName != null)
                        cap = eventName;
                    
                    typeFChartDataXml = "<chart "
                    		+ "chartbottommargin='0' "
                    		+ "charttopmargin='0' "
                    		+ "unescapeLinks='0' "
                            + "showValues='1' "
                            + "canvasborderthickness='1' "
                            + "canvasbordercolor='#99bbe8' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_Column2D_nobg  
                            + ">";

                    var labels = "";

                    setListLen(list.length);
                    for (var index = 0; index < list.length; index++) {
                        labels += "<set label='" + list[index].eventName + "' value='" + list[index].eventCount + "' link='j-searchMeterByEventList-" + list[index].eventName + "' />";
                    }

                    if (list.length == 0) {
                        labels  += "<set label=' ' value='0' />";
                    }

                    typeFChartDataXml += labels + "</chart>";
                    fChartRender();
                }
        );

        hide();
    }

    function fChartRender() {
	console.log(typeFChartDataXml);
        if ($('#typeFChartDiv').is(':visible')) {
            // 전역 listLen을 이용해 높이를 동적으로 설정
            if ( FusionCharts( "typeFChartId" ) ) FusionCharts( "typeFChartId" ).dispose();
            if(listLen <= 10)
            	typeFChart = new FusionCharts({
            		id: 'typeFChartId',
        			type: 'Bar2D',
        			renderAt : 'typeFChartDiv',
        			width : '100%',
        			height : '290',
        			dataSource : typeFChartDataXml
        		}).render();
            else{
                var fChartHeight = 290 + (listLen-10)*20;
            	typeFChart = new FusionCharts({
            		id: 'typeFChartId',
        			type: 'Bar2D',
        			renderAt : 'typeFChartDiv',
        			width : '100%',
        			height : fChartHeight,
        			dataSource : typeFChartDataXml
        		}).render();
            }
        }
    }

    function validateNumber(src) {
        var pattern = new RegExp(/^[0-9]+$/);
        if (!pattern.test(src.value)) {
            src.value = src.value.replace(/[^0-9]/g, "");
        }
    }

    //컬럼 Tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "" && metadata != null) {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }

    //meterEventLogMeterByEvent 그리드
    var meterEventLogMeterByEventGridStore;
    var meterEventLogMeterByEventGridColModel;
    var meterEventLogMeterByEventGridOn = false;
    var meterEventLogMeterByEventGrid;

    function getMeterEventLogMeterByEventGrid() {
        var arrayObj = getConditionArray();
        var fmtMessage = getFmtMessage();
        var width = $("#meterEventLogMeterByEventDiv").width();
        var height = 0;
       	
        meterEventLogMeterByEventGridStore = new Ext.data.JsonStore({
        	autoLoad : {params:{start: 0, limit: 10}},
            url : "${ctx}/gadget/device/getMeterEventLogMeterByEventGridData.do",
            baseParams : {
                searchStartDate : arrayObj[0],
                searchEndDate   : arrayObj[1],
                searchDateType  : arrayObj[2],
                locationId      : arrayObj[3],
                eventName       : seleventName,
                meterType       : arrayObj[5],
                meterId         : arrayObj[6],
                occurFreq       : arrayObj[7],
                supplierId      : arrayObj[8],
                gs1             : arrayObj[10],
                pageSize        : 10
            },
            totalProperty : 'total',
            root : 'gridDatas',
            fields : [
                {name : 'ROWNUM'      , type : 'String'},
                {name : 'EVENTNAME'   , type : 'String'},
                {name : 'LOCATIONNAME', type : 'String'},
                {name : 'METERCOUNT'  , type : 'String'},
                {name : 'METERID'     , type : 'String'},
                {name : 'GS1'         , type : 'String'},
                {name : 'METERTYPE'   , type : 'String'}
            ],
            listeners : {
                beforeload: function(store, options) {
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                    });
                }
            }
        });

        meterEventLogMeterByEventGridColModel = new Ext.grid.ColumnModel({
            columns: [
                {header: fmtMessage[0],
                    tooltip: fmtMessage[0],
                    dataIndex: 'ROWNUM',
                    align:'center',
                    width: 60 ,
                    renderer: function(value, me, record, rowNumber, rowIndex, store) {
                        var st = record.store;
                        if (st.lastOptions.params && st.lastOptions.params.start != undefined &&
                                st.lastOptions.params.limit != undefined) {
                            var page = Math.floor(st.lastOptions.params.start/st.lastOptions.params.limit);
                            var limit = st.lastOptions.params.limit;
                            return Ext.util.Format.number((limit*page) + rowNumber+1,numberFormat);
                        }
                    }
                }
                ,{header: fmtMessage[3],
                    tooltip: fmtMessage[3],
                    dataIndex: 'EVENTNAME',
                    align:'center',
                    width: 120
                }
                ,{header: fmtMessage[5],
                    tooltip: fmtMessage[5],
                    dataIndex: 'METERID',
                    align: 'center'
                }
                ,{header: fmtMessage[20],
                    tooltip: fmtMessage[20],
                    dataIndex: 'GS1',
                    align: 'center'
                }
                ,{header: fmtMessage[10],
                    tooltip: fmtMessage[10],
                    dataIndex: 'METERCOUNT',
                    align: 'right'
                }
                ,{header: fmtMessage[4],
                    tooltip: fmtMessage[4],
                    dataIndex: 'LOCATIONNAME',
                    align: 'center'
                }
                ,{header: fmtMessage[6],
                    tooltip: fmtMessage[6],
                    dataIndex: 'METERTYPE',
                    align: 'center'
                }
            ],
            defaults : {
                sortable : true
                ,menuDisabled : true
                ,width : ((width-60-chromeColAdd)/6) 
                ,renderer : addTooltip
            }
        });

        if (meterEventLogMeterByEventGridOn == false) {
            meterEventLogMeterByEventGrid = new Ext.grid.GridPanel({
                id : 'meterEventLogMeterByEventGrid',
                store : meterEventLogMeterByEventGridStore,
                colModel : meterEventLogMeterByEventGridColModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {

                        rowselect : function(selectionModel, columnIndex, value) {
                            var param = value.data;
                            searchEventByMeterList(param.EVENTNAME,param.METERID);
                        }
                    }
                }),
                autoScroll : false,
                width : width,
                height : 290,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'meterEventLogMeterByEventDiv',
                viewConfig : {
                    forceFit : true,
                    scrollOffset : 1,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                } ,
                bbar : new Ext.PagingToolbar({
                    pageSize : 10,
                    store : meterEventLogMeterByEventGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            meterEventLogMeterByEventGridOn = true;
        } else {
            meterEventLogMeterByEventGrid.setWidth(width);
            meterEventLogMeterByEventGrid.reconfigure(meterEventLogMeterByEventGridStore, meterEventLogMeterByEventGridColModel);
            var bottomToolbar = meterEventLogMeterByEventGrid.getBottomToolbar();
            bottomToolbar.bindStore(meterEventLogMeterByEventGridStore);
        }
    }

    // meterEventLogEventByMeter 그리드
    var meterEventLogEventByMeterGridStore;
    var meterEventLogEventByMeterGridColModel;
    var meterEventLogEventByMeterGridOn = false;
    var meterEventLogEventByMeterGrid;

    function getMeterEventLogEventByMeterGrid() {
        var arrayObj   = getConditionArray();
        var fmtMessage = getFmtMessage();
        console.log(fmtMessage);
        var width = $("#meterEventLogEventByMeterDiv").width();

        meterEventLogEventByMeterGridStore = new Ext.data.JsonStore({
            autoLoad : {params:{start: 0, limit: 10}},
            url : "${ctx}/gadget/device/getMeterEventLogEventByMeterGridData.do",
            baseParams : {
                searchStartDate : arrayObj[0],
                searchEndDate   : arrayObj[1],
                searchDateType  : arrayObj[2],
                locationId      : arrayObj[3],
                eventName       : seleventName,
                meterType       : arrayObj[5],
                meterId         : arrayObj[6],
                occurFreq       : arrayObj[7],
                supplierId      : arrayObj[8],
                activatorId     : arrayObj[9],
                gs1             : arrayObj[10],
                pageSize        : 10
            },
            totalProperty : 'total',
            root : 'gridDatas',
            fields : [
                { name: 'ROWNUM'        , type: 'String'},
                { name: 'OPENTIME'      , type: 'String' },
                { name: 'WRITETIME'     , type: 'String' },
                { name: 'EVENTNAME'     , type: 'String' },
                { name: 'LOCATIONNAME'  , type: 'String' },
                { name: 'METERID'       , type: 'String' },
                { name: 'GS1'       , type: 'String' },
                { name: 'METERTYPE'     , type: 'String' },
                { name: 'OBISCODE'      , type: 'String' },
                { name: 'MESSAGE'       , type: 'String' },
                /*
                { name: 'eventModel'     , type: 'String' },
                { name: 'eventValue'     , type: 'String' }
                { name: 'troubleAdvice' , type: 'String' } 
                */
            ],
            listeners : {
                beforeload : function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                    });
                }
            }
        });

        meterEventLogEventByMeterGridColModel = new Ext.grid.ColumnModel({
            columns : [
                {header: fmtMessage[0],
                    tooltip: fmtMessage[0],
                    dataIndex: 'ROWNUM',
                    align:'center',
                    width: 60 ,
                    renderer: function(value, me, record, rowNumber, rowIndex, store) {
                        var st = record.store;
                        if (st.lastOptions.params && st.lastOptions.params.start != undefined &&
                                st.lastOptions.params.limit != undefined) {
                            var page = Math.floor(st.lastOptions.params.start/st.lastOptions.params.limit);
                            var limit = st.lastOptions.params.limit;
                            return Ext.util.Format.number((limit*page) + rowNumber+1,numberFormat);
                        }
                    }
                }
                ,{header: fmtMessage[1],
                    tooltip: fmtMessage[1],
                    dataIndex: 'OPENTIME',
                    align:'center',
                    width: width/10-20
                }
                ,{header: "OBIS Code",
                    tooltip: "OBIS Code",
                    width: width/10+10,
                    dataIndex: 'OBISCODE',
                    align: 'center'
                }
                ,{header: fmtMessage[2],
                    tooltip: fmtMessage[2],
                    width: width/10+10,
                    dataIndex: 'WRITETIME',
                    align: 'center'
                }
                ,{header: fmtMessage[3],
                    tooltip: fmtMessage[3],
                    width: width/10+10,
                    dataIndex: 'EVENTNAME',
                    align: 'center'
                }
                ,{header: fmtMessage[4],
                    tooltip: fmtMessage[4],
                    width: width/10,
                    dataIndex: 'LOCATIONNAME',
                    align: 'center'
                }
                ,{header: fmtMessage[5],
                    tooltip: fmtMessage[5],
                    width: width/10,
                    dataIndex: 'METERID',
                    align: 'center'
                }
                ,{header: fmtMessage[20],
                    tooltip: fmtMessage[20],
                    width: width/10,
                    dataIndex: 'GS1',
                    align: 'center'
                }
                ,{header: fmtMessage[6],
                    tooltip: fmtMessage[6],
                    width: width/10,
                    dataIndex: 'METERTYPE',
                    align: 'center'
                }
                ,{header: fmtMessage[7],
                    tooltip: fmtMessage[7],
                    width: width/10,
                    dataIndex: 'MESSAGE',
                    align: 'center'
                }
                 /* ,{header: "eventModel",
                    tooltip: "eventModel",
                    width: width/9,
                    dataIndex: 'eventModel',
                    align: 'center'
                }
                 ,{header: "eventValue",
                     tooltip: "eventValue",
                     width: width/9,
                     dataIndex: 'eventValue',
                     align: 'center'
                 } */
            ],
            defaults : {
                sortable : true
                ,menuDisabled : true
                ,width : ((width-30)/10)-chromeColAdd
                ,renderer : addTooltip
            }
        });

        if (meterEventLogEventByMeterGridOn == false) {
            meterEventLogEventByMeterGrid = new Ext.grid.GridPanel({
                id : 'meterEventLogEventByMeterGrid',
                store : meterEventLogEventByMeterGridStore,
                colModel : meterEventLogEventByMeterGridColModel,
                autoScroll : false,
                width : width,
                height : 290,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'meterEventLogEventByMeterDiv',
                viewConfig : {
                    forceFit : true,
                    scrollOffset : 1,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                } ,
                bbar : new Ext.PagingToolbar({
                    pageSize : 10,
                    store : meterEventLogEventByMeterGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            meterEventLogEventByMeterGridOn = true;
        } else {
            meterEventLogEventByMeterGrid.setWidth(width);
            meterEventLogEventByMeterGrid.reconfigure(meterEventLogEventByMeterGridStore, meterEventLogEventByMeterGridColModel);
            var bottomToolbar = meterEventLogEventByMeterGrid.getBottomToolbar();
            bottomToolbar.bindStore(meterEventLogEventByMeterGridStore);
        }
    }

    //report window(Excel)
    var win;
    function openExcelReport(type) {
        var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();
        var event;
        var meter;
        var gs1;

        if (type == "event") {
            event = ($('#eventName').val()=="")?"":$('#eventName').val();
            meter = (activatorId=="")?"":activatorId;
            gs1   = ($('#gs1').val()=="")?"":$('#gs1').val();
        } else {
            event = "";
            meter = "";
            gs1   = "";
        }

        obj.condition  = getConditionArray();
        obj.fmtMessage = getFmtMessage();
        obj.type       = type;
        obj.eventName  = event;
        obj.meterId    = meter;
        obj.gs1        = meter;
        if(win)
            win.close();
        win = window.open("${ctx}/gadget/device/meterEventLogExcelDownloadPopup.do", "MeterEventLogExcel", opts);
        win.opener.obj = obj;
    }

    //체크박스 모델에 리스너 등록.
    meterEventLogProfileCheckSelModel = new Ext.grid.CheckboxSelectionModel({
        checkOnly : true,
        listeners : {
            beforerowselect : function(selectionmodel,rowIndex,store) {
                var fmtMessage =  getFmtMessage();
                var checkedList = selectionmodel.getSelections();
                var checkboxCnt = checkedList.length+1;

                // 고객사별 체크박스 버튼 갯수 지정 함수
                $(function(){
                    // 이라크 MOE가 아닐 경우, 체크박스 10개 선택 제한
                    if(supplier_Name != 'MOE' || supplier_Name != 'SORIA'){
                    }else{
                    	if (checkboxCnt > 10) {
                            Ext.Msg.alert("<fmt:message key="aimir.error"/>", fmtMessage[16]);
                            // checkedList[10].clearSelections();
                            selectionmodel.deselectRow(10);
                            selectionmodel.resumeEvents();
                            return false;
                        }
                    }
                });
            },
            //체크박스 선택시
            rowselect : function(selectionmodel, rowIndex,store) {
                meterEventParams = Array();
                var checkedList = selectionmodel.getSelections();
                meterEventParams = checkedList;

            },
            //체크박스 선택 해지시
            rowdeselect : function(selectionmodel, rowIndex,  record, store) {
                meterEventParams = Array();
                var checkedList = selectionmodel.getSelections();
                meterEventParams = checkedList;
            }
        }
    });

    // Filter Setting 탭/meterEventLogProfileMax 그리드
    var meterEventLogProfileMaxGridStore;
    var meterEventLogProfileMaxGridColModel;
    var meterEventLogProfileMaxGridOn = false;
    var meterEventLogProfileMaxGrid;
    var meterEventLogProfileCheckSelModel;
    function getMeterEventLogProfileMaxGrid() {
        var fmtMessage = getFmtMessage();
        var width = $("#meterEventLogProfileMaxDiv").width();

        meterEventLogProfileMaxGridStore = new Ext.data.JsonStore({
            autoLoad : true,
            url: "${ctx}/gadget/device/getMeterEventLogProfileData.do",
            root:'gridData',
            fields: [
                { name: 'rownum'        , type: 'Integer'},
                { name: 'meterEventName', type: 'String' }
            ],
            listeners : {
                load : function(store, record, options) {
                    var data = store.data.items;
                    var recs = [];
                    Ext.each(data, function(item, index) {
                        if (item.json.hasProfile == "Y") {
                            recs.push(index);
                        }
                    });
                    meterEventLogProfileMaxGrid.getSelectionModel().selectRows(recs);
                }
            }
        });

        // GET을 POST로 변경
        meterEventLogProfileMaxGridStore.proxy.conn.method = 'POST';

        meterEventLogProfileMaxGridColModel = new Ext.grid.ColumnModel({
            columns : [
                meterEventLogProfileCheckSelModel,
                {header: fmtMessage[0],
                    tooltip: fmtMessage[0],
                    dataIndex: 'rownum',
                    align:'center',
                    width: 100,
                    renderer: function(value, me, record, rowNumber, rowIndex, store) {
                        return Ext.util.Format.number(rowNumber+1,numberFormat);
                    }
                }
                ,{header: fmtMessage[3],
                    tooltip: fmtMessage[3],
                    dataIndex: 'meterEventName',
                    align:'left',
                    id:'meterEventName',
                    width: width-120
                }
            ],
            defaults : {
                sortable : true
                ,menuDisabled : true
                ,width : ((width-30)/2)-chromeColAdd
                ,renderer : addTooltip
            }
        });

        if (meterEventLogProfileMaxGridOn == false) {
            meterEventLogProfileMaxGrid = new Ext.grid.GridPanel({
                id : 'meterEventLogProfileMaxGrid',
                store : meterEventLogProfileMaxGridStore,
                colModel : meterEventLogProfileMaxGridColModel,
                selModel : meterEventLogProfileCheckSelModel,
                autoScroll : false,
                width : width,
                height : 690,
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'meterEventLogProfileMaxDiv',
                viewConfig : {
                    forceFit : true,
                    scrollOffset : 1,
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                }
            });
            meterEventLogProfileMaxGridOn = true;
        } else {
            meterEventLogProfileMaxGrid.setWidth(width);
            meterEventLogProfileMaxGrid.reconfigure(meterEventLogProfileMaxGridStore, meterEventLogProfileMaxGridColModel);
        }
    }

    function saveList() {
        var fmtMessage = getFmtMessage();
        var allRemove;
        var meterEventNames = Array();
        for (var i = 0; i < meterEventParams.length; i++) {
            meterEventNames.push(meterEventParams[i].json.meterEventName);
        }

        if (meterEventNames.length == 0) {
            meterEventNames[0] = "meterEventName";
            allRemove = "yes";
        } else {
            allRemove = "no";
        }

        // GET을 POST로 변경
        $.ajax({
            type : "POST",
            url : '${ctx}/gadget/device/updateMeterEventLogProfileData.do',
            async : false,
            data : {
                meterEventNames : meterEventNames.toString(),
                allRemove : allRemove
            },
            success :function(returnData) {
                if (returnData.status == "success") {
                    Ext.Msg.alert("", fmtMessage[14]);
                } else {
                    Ext.Msg.alert("", fmtMessage[15]);
                }
            }
        });

        // GET을 POST로 변경 전 원본 소스
        /* $.getJSON('${ctx}/gadget/device/updateMeterEventLogProfileData.do',
         {
         meterEventNames : meterEventNames.toString(),
         allRemove : allRemove
         },function(returnData) {
         if (returnData.status == "success") {
         Ext.Msg.alert("", fmtMessage[14]);
         } else {
         Ext.Msg.alert("", fmtMessage[15]);
         }
         }
         ); */
    }

    /*]]>*/
    </script>
</head>
<body>
<!--tab-->
<div id="gad_sub_tab">
    <ul>
        <li><a href="javascript:changeTab('eventType')" name="eventTypeTab" id="eventTypeTab" class="current"><fmt:message key="aimir.device.byEventType"/></a></li>
        <li><a href="javascript:changeTab('filterSet')" name="filterSetTab" id="filterSetTab"><fmt:message key="aimir.device.filterSetting"/></a></li>
    </ul>
</div>
<!--// tab-->

<!-- search -->


<div id="typeSearch">
    <!-- term search -->
    <div class="dayoptions-bt">
        <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
    </div>
    <div class="dashedline"></div>
    <!--// term search -->

    <div class="add_search">
        <div style="display:block;">
            <table class="billing">
                <tr>
                    <th><fmt:message key="aimir.location"/></th>
                    <td><input type="text" id="searchWord" name="searchWord" style="width:140px" value="<fmt:message key='aimir.location'/>" />
                        <input type="hidden" id="locationId" value="" />

                    </td>
                    <th><fmt:message key="aimir.device.eventName"/></th>
                    <td colspan="5"><select name="eventName" id="eventName" style="width:300px;"><option value=""></option></select></td>
                </tr>
                <tr>
                    <th><fmt:message key="aimir.metertype"/></th>
                    <td><select name="meterType" id="meterType" style="width:150px;"><option value=""></option></select></td>
                    <th><fmt:message key="aimir.meterid"/></th>
                    <td><input type="text" name="meterId" id="meterId" style="width:120px;"/></td>
                    <th><fmt:message key="aimir.shipment.gs1"/></th>
                    <td><input type="text" name="gs1" id="gs1" style="width:120px;"/></td>
                    <th><fmt:message key="aimir.device.occurFreq"/></th>
                    <td><input type="text" name="occurFreq" id="occurFreq" style="width:50px;" onchange="validateNumber(this);"/></td>
                    <th><fmt:message key="aimir.device.occurMore"/></th>
                    <td><span class="am_button"><a href="javascript:searchList();" ><fmt:message key="aimir.button.search" /></a></span></td>
	            	<td colspan="4">
	            		<div id ="searchTipDiv" class="btn_bluegreen">
	            			<ul><li>
	            				<a href="javascript:openSearchTip()" class="on">
	            					<img src="../../js/extjs/resources/images/default/window/icon-warning.gif" style="width:10px;"/> Search Tips
	            				</a>
	            			</li></ul>
	            		</div>
	        			<div id='serchTipWin'></div>
	            	</td>
                </tr>
            </table>
            <div id="treeDivAOuter" class="tree-billing auto" style="display:none">
                <div id="treeDivA"></div>
            </div>
        </div>
        <!--// tab 1 search -->
    </div>

</div>
<!--// search -->

<!-- tab 1 chart -->
<div id="typeDataDiv" class="gadget_body3" style="display:none;">
    <!-- <div class="overflow_hidden padding-b10px"> -->
    <div class="overflow_hidden">
		<div id="chartWrapper" class="chart_left" >
			<table style="margin:auto"><tr ><td id="chartTitle"><fmt:message key="aimir.meterCountByEventName"/></td></tr></table>
        	<div id="typeFChartDiv" class="chart_left"></div>
        </div>

        <div id="termFChartDiv" class="chart_right">
            <span class="am_button" style="float: right; margin-right: 3px; margin-bottom: 3px;"><a href="javascript:openExcelReport('meter');" ><fmt:message key="aimir.button.excel"/></a></span>
            <table style="margin:auto"><tr ><td id="gridTitle"><fmt:message key="aimir.meterEventByMeterID"/></td></tr></table>
            <div id="meterEventLogMeterByEventDiv" class="clear both"></div>
        </div>

    </div>
    <div id="typeGridDiv" class="clear">
        <span class="am_button" style="float: right; margin-right: 3px; margin-bottom: 3px;"><a href="javascript:openExcelReport('event');" ><fmt:message key="aimir.button.excel"/></a></span>
        <div id="meterEventLogEventByMeterDiv" class="clear both"><b><fmt:message key="aimir.meterEventLog"/></b></div>
    </div>
</div>
<!--// tab 1 chart -->


<!-- tab 2 chart -->
<div id="filterSetDiv" class="gadget_body3" style="display:none;">
    <span id="applyBtn" class="am_button" style="float: right; margin-right: 3px; margin-bottom: 3px;"><a href="javascript:saveList();" ><fmt:message key="aimir.button.apply"/></a></span>
    <div id="meterEventLogProfileMaxDiv" class="clear both"></div>
</div>

<!--// tab 2 chart -->
</body>
</html>
