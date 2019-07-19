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

        TABLE{border-collapse: collapse; width:auto;}
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
        @media screen and (-webkit-min-device-pixel-ratio:0) {
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
            .tree .ltr ins { margin:0 4px 0 5px !important;}
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
    var typfFChart;

    var flex;
    var supplierId = "";
    var meterEventParams = Array();
    var chromeColAdd = 2;
    var supplier_Name;
    // 수정권한
    var editAuth = "${editAuth}";

    $(document).ready(function(){
    	Ext.Ajax.timeout = 300000;
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

        if(!(meterEventLogProfileMiniGrid === undefined)){
            meterEventLogProfileMiniGrid.destroy();
        }

        meterEventLogProfileMiniGridOn = false;
        getMeterEventLogProfileMiniGrid();

    });

    function getEditAuth() {
        return editAuth;
    }

    // init
    function pageInit(){
        hide();
        locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
        changeTab("eventType");
        //searchData();
    }

    function searchData(){
        updateTypeFChart();
    };

    function getFmtMessage(){
        var fmtMessage = new Array();
        var idx = 0;
        fmtMessage[idx++] = "<fmt:message key="aimir.checkBox"/>";              // 체크
        fmtMessage[idx++] = "<fmt:message key="aimir.number"/>";                // 번호
        fmtMessage[idx++] = "<fmt:message key="aimir.device.eventName"/>";      // Event Name
        fmtMessage[idx++] = "<fmt:message key="aimir.button.search"/>";         // 검색
        fmtMessage[idx++] = "<fmt:message key="aimir.button.apply"/>";          // 적용
        fmtMessage[idx++] = "<fmt:message key="aimir.save"/>";                  // 저장되었습니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.save.error"/>";            // 저장되지 않았습니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.device.max10"/>";          // ! 최대 10개만 설정 가능합니다.
        fmtMessage[idx++] = "<fmt:message key="aimir.env.error"/>";             // 사용할수 없는 환경입니다.

        return fmtMessage;
    }

    function changeTab(tabId){

        if(tabId == 'eventType') {
            if(!$('#typeFChartDiv').is(':visible')) {
                $('#filterSetDiv').hide();
                $('#filterSetTab').removeClass('current');

                $('#typeSearch').show();
                $('#eventTypeTab').addClass('current');
                $('#typeFChartDiv').show();
                //searchData();
            }
        } else if(tabId == 'filterSet') {

            if(!$('#filterSetDiv').is(':visible')) {
                $('#typeSearch').hide();
                $('#typeFChartDiv').hide();

                $('#filterSetDiv').show();

                $('#eventTypeTab').removeClass('current');
                $('#filterSetTab').addClass('current');

                getMeterEventLogProfileMiniGrid();
                if(getEditAuth() != "true"){
                    $('#applyBtn').hide();
                }
            }
        }
    }

    function updateTypeFChart() {
        emergePre();

        $.getJSON('${ctx}/gadget/device/getMeterEventLogMiniChartData.do'
                ,{searchStartDate:$('#searchStartDate').val(),
                    searchEndDate:$('#searchEndDate').val(),
                    searchDateType:$('#searchDateType').val(),
                    locationId:$('#locationId').val(),
                    supplierId:supplierId}
                ,function(json) {
                    var list = json.chartData;
                    var count = '<fmt:message key="aimir.count"/>';

                    typeFChartDataXml = "<chart "
                             + "showValues='0' "
                            + "useRoundEdges='0' "
                            + "yAxisMaxValue='10' "
                            + "yAxisName='" + count + "' "
                            + "maxLabelWidthPercent='50' "
                            + "plotSpacePercent ='60' "
                            + "showPlotBorder ='true' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_Column2D_nobg
                            + ">";

                    var labels = "";

                    for( var index = 0;index<list.length;index++){
                        if(index != "indexOf") {
                            labels  += "<set label='" + list[index].eventName + "' value='" + list[index].eventCount + "' />";
                        }
                    }

                    if(list.length == 0) {
                        labels  += "<set label=' ' value='0' />";
                    }

                    typeFChartDataXml += labels + "</chart>";

                    fChartRender();
                }
        );

        hide();
    }

    function fChartRender() {
        if($('#typeFChartDiv').is(':visible')) {
        	typeFChart = new FusionCharts({
        		id: 'typeFChartId',
    			type: 'Bar2D',
    			renderAt : 'typeFChartDiv',
    			width : $('#typeFChartDiv').width(),
    			height : '195',
    			dataSource : typeFChartDataXml
    		}).render();
        	
/*         	typeFChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Bar2D.swf", "typeFChartId", $('#typeFChartDiv').width(), "350", "0", "0");
            typeFChart.setDataXML(typeFChartDataXml);
            typeFChart.setTransparent("transparent");
            typeFChart.render("typeFChartDiv"); */
        }
    }

    //체크박스 모델에 리스너 등록.
    meterEventLogProfileCheckSelModel = new Ext.grid.CheckboxSelectionModel({
        // singleSelect: false ,//다수 선택 가능.단 다른 컬럼 선택시 해지됨.
        checkOnly:true,
        listeners:{

            beforerowselect:function(selectionmodel,rowIndex,store){
                var fmtMessage =  getFmtMessage();
                var checkedList = selectionmodel.getSelections();
                var checkboxCnt = checkedList.length+1;

                // 고객사별 체크박스 버튼 갯수 지정 함수
                $(function(){

                    // 이라크 MOE가 아닐 경우, 체크박스 10개 선택 제한
                    if(supplier_Name == 'MOE' || supplier_Name == 'SORIA'){
                        
                    } else {
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
            rowselect:function(selectionmodel, rowIndex,store){
                meterEventParams = Array();
                var checkedList = selectionmodel.getSelections();
                meterEventParams = checkedList;
            },
            //체크박스 선택 해지시
            rowdeselect:function(selectionmodel, rowIndex,  record,store){
                meterEventParams = Array();
                var checkedList = selectionmodel.getSelections();
                meterEventParams = checkedList;
            }
        }
    });

    // Filter Setting 탭/meterEventLogProfileMini 그리드
    var meterEventLogProfileMiniGridStore;
    var meterEventLogProfileMiniGridColModel;
    var meterEventLogProfileMiniGridOn = false;
    var meterEventLogProfileMiniGrid;
    var meterEventLogProfileCheckSelModel;
    function getMeterEventLogProfileMiniGrid(){

        var fmtMessage  = getFmtMessage();
        var width = $("#meterEventLogProfileMiniDiv").width();

        meterEventLogProfileMiniGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/device/getMeterEventLogProfileData.do",
            root:'gridData',
            fields: [
                { name: 'rownum'        , type: 'Integer'},
                { name: 'meterEventName', type: 'String' }
            ],
            listeners: {
                load : function(store, record, options) {
                    var data = store.data.items;
                    var recs = [];
                    Ext.each(data, function(item, index) {
                        if (item.json.hasProfile == "Y") {
                            recs.push(index);
                        }
                    });
                    meterEventLogProfileMiniGrid.getSelectionModel().selectRows(recs);
                }
            }
            // meterEventLogMini Filter 체크박스 갱신 오류 수정 전 원본
            /* listeners: {
             beforeload: function(store, options){
             options.params || (options.params = {});
             Ext.apply(options.params, {
             page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
             });
             }
             } */
        });

        meterEventLogProfileMiniGridColModel = new Ext.grid.ColumnModel({
            columns: [
                meterEventLogProfileCheckSelModel,
                {header: fmtMessage[1],
                    tooltip: fmtMessage[1],
                    dataIndex: 'rownum',
                    align:'center',
                    width: 50,
                    renderer: function(value, me, record, rowNumber, rowIndex, store) {
                        var st = record.store;
                        if (st.lastOptions.params && st.lastOptions.params.start != undefined &&
                                st.lastOptions.params.limit != undefined) {
                            var page = Math.floor(st.lastOptions.params.start/st.lastOptions.params.limit);
                            var limit = st.lastOptions.params.limit;
                            return (limit*page) + rowNumber+1;
                        }
                    }
                }
                ,{header: fmtMessage[2],
                    tooltip: fmtMessage[3],
                    dataIndex: 'meterEventName',
                    align:'left',
                    id:'meterEventName',
                    width: width-80
                }
            ],
            defaults: {
                sortable: true
                ,menuDisabled: true
                ,width: ((width-30)/2)-chromeColAdd

            },

        });
        if (meterEventLogProfileMiniGridOn == false) {
            meterEventLogProfileMiniGrid = new Ext.grid.GridPanel({
                id: 'meterEventLogProfileMiniGrid',
                store: meterEventLogProfileMiniGridStore,
                colModel : meterEventLogProfileMiniGridColModel,
                selModel : meterEventLogProfileCheckSelModel,
                autoScroll: false,
                width: width,
                height: 370,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'meterEventLogProfileMiniDiv',
                viewConfig: {
                    forceFit:true,
                    scrollOffset: 1,
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: meterEventLogProfileMiniGridStore,
                    displayInfo: true,

                    displayMsg: ' {0} - {1} / {2}'
                })
                ,
                listeners: {
                    afterrender: function() {

                        var me = this;
                        console.log("me:",me);
                        meterEventLogProfileMiniGridStore.on('load', function(){
                            var data = me.getStore().data.items;
                            console.log("data:",data);
                            var recs = [];
                            Ext.each(data, function(item, index){
                                if (item.json.hasProfile=="Y") {
                                    recs.push(index);
                                }
                            });
                            me.getSelectionModel().selectRows(recs);
                        })
                    }
                }
            });
            meterEventLogProfileMiniGridOn  = true;
        } else {

            meterEventLogProfileMiniGrid.setWidth(width);
            meterEventLogProfileMiniGrid.reconfigure(meterEventLogProfileMiniGridStore, meterEventLogProfileMiniGridColModel);
            var bottomToolbar = meterEventLogProfileMiniGrid.getBottomToolbar();
            bottomToolbar.bindStore(meterEventLogProfileMiniGridStore);
        }
    }

    function saveList(){
        var fmtMessage  = getFmtMessage();
        var allRemove;
        var meterEventNames = Array();

        for(var i=0;i<meterEventParams.length;i++){
            meterEventNames.push(meterEventParams[i].json.meterEventName);
        }

        if(meterEventNames.length == 0){
            meterEventNames[0] = "meterEventName";
            allRemove = "yes";
        }else{
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
                    Ext.Msg.alert("", fmtMessage[5]);
                } else {
                    Ext.Msg.alert("", fmtMessage[6]);
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
        <!-- tab 1 search -->
        <div style="display:block">
            <table class="billing">
                <tr>
                    <!--            	<th><fmt:message key="aimir.location"/></th>-->
                    <th>
                        <input type="text" id="searchWord" name="searchWord" style="width:140px" value="<fmt:message key='aimir.location'/>" />
                        <input type="hidden" id="locationId" value="" />

                    </th>
                    <th>
                        <span class="am_button"><a href="javascript:searchData();" ><fmt:message key="aimir.button.search" /></a></span>
                    </th>
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

<!-- chart -->
<div id="typeFChartDiv" class="gadget_body3"></div>
<div id="filterSetDiv" class="gadget_body3" style="display:none;">
    <span id="applyBtn" class="am_button" style="float: right; margin-right: 3px; margin-bottom: 3px;"><a href="javascript:saveList();" ><fmt:message key="aimir.button.apply"/></a></span>
    <div id="meterEventLogProfileMiniDiv" class="clear both"></div>
</div>
<!--// chart -->
</body>
</html>
