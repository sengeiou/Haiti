
<!--
가젯 : Communication Report
설명 : LP Interval, 정전을 고려한 검침율 조사
sejin han
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title>Communication Report</title>

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
    </style>

    <!-- LIB -->
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
</head>
<body>

<!-- SCRIPT -->
<script type="text/javascript" charset="utf-8">

    var supplierId = "${supplierId}";
    var numberFormat = "";

    /**
     * 공통 모듈
     */
    $(document).ready(function () {
        // 유저 세션 정보 가져오기
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                        //검색 옵션 초기화
                        clearSearchOptions();
                    }
                }
        );

        //달력 모듈이 완료될때까지 지연
        setTimeout(function(){getMeterIdGrid();}, 600);
        //getValidMeteringRateGrid();
    });

    // 검색 버튼
    var meteringRateSearch = function(){
        // 그리드 호출
        getValidMeteringRateGrid();
    }

    // 검색 초기화 버튼
    var clearSearchOptions = function(){
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();

        if(("" + month).length == 1) month = "0" + month;
        if(("" + day).length == 1) day = "0" + day;

        var setDate      = year + "" + month + "" + day;
        var dateFullName = "";
        var locDateFormat = "yymmdd";

        // 날짜를 지역 날짜 포맷으로 변경
        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    dateFullName = json.localDate;
                    $('#historyStartDate').val(dateFullName);
                    $('#historyEndDate').val(dateFullName);
                });
        $('#historyStartDate').val(setDate);
        $('#historyStartDateHidden').val(setDate);

        $('#historyStartDate').datepicker({
            showOn: 'button',
            buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
            buttonImageOnly: true,
            dateFormat:locDateFormat,
            onSelect: function(dateText, inst) { modifyDate(dateText, inst);}
        });

        // TextBox 초기화
        $('#meteridbox').val('');
    }

    // datepicker로 선택한 날짜의 포맷 변경
    function modifyDate(setDate, inst){
        var dateId = '#' + inst.id;

        var dateHiddenId = '#' + inst.id + 'Hidden';
        $(dateHiddenId).val($(dateId).val());

        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    $(dateId).val(json.localDate);
                    $(dateId).trigger('change');
                });
    }

    // 입력된 검색 조건 확인
    var getSearchCondition = function(){
        var arrayObj = Array();
        //cache
        arrayObj[0] = Math.random();
        //conditions
        arrayObj[1] = $('#meteridbox').val().trim();
        arrayObj[2] = $('#historyStartDateHidden').val();

        return arrayObj;
    }

    // 시간별 LP,Event Grid
    var meteringRateStore;
    var meteringRateCol;
    var meteringRateGrid;
    var getValidMeteringRateGrid = function(){
        var conditionArray = getSearchCondition();
        var grWidth = $('#ReportGridDiv').width()*0.3;
        var pageSize = 10;

        meteringRateStore = new Ext.data.JsonStore({
            autoLoad : true,
            url: "${ctx}/gadget/report/getValidMeteringRate.do",
            baseParams : {
                'supplierId' : supplierId,
                'mdevId' : conditionArray[1],
                'searchDate' : conditionArray[2]
            },
            root: 'calc',
            fields: ['hhmmss','name'],
            listeners:{
                load: function(mStore, mRecord, mOptions){
                    var rec = mRecord[mRecord.length-1].json;
                    var validRate = Number(Number(rec.VALUE_CNT)/(Number(rec.LP_TOTAL)-Number(rec.DOWN_LP)))*100;
                    var detail = '<label class="graybold11pt"> 필요 LP 개수 : '+rec.LP_TOTAL+'</label><br>'
                            + '<label class="graybold11pt"> 정상 LP : '+rec.VALUE_CNT+'</label><br>'
                            + '<label class="graybold11pt"> 일반 누락 : '+rec.MISSING_LP+'</label><br>'
                            + '<label class="graybold11pt"> 정전 누락 : '+rec.DOWN_LP+'</label><br>'
                            + '<label class="graybold11pt"> 정전 제외 검침율 : '+validRate.toFixed(3)+'</label><br>';
                    $('#rateGrid').html(detail);

                }
            }
        });

        meteringRateCol = new Ext.grid.ColumnModel({
           columns: [
               {header: 'Time', dataIndex: 'hhmmss', renderer: convertDate},
               {header: 'LP/EVENT', dataIndex: 'name', renderer: convertColor}
           ],
            defaults: {
                sortable : false,
                menuDisable : true,
                align : 'center',
                width : 120
            },
        });

        function convertDate(val){
            if(val.length>6){
                val = val.substring(1);
                return val;
            }
            return val;
        }

        function convertColor(val){
            if(val.search('UP') >= 0){
                return '<p style="color:green;">'+val+'</p>';
            }else if(val.search('DOWN') >= 0){
                return '<p style="color:brown;">'+val+'</p>';
            }
            return val;
        }

        meteringRateGrid = new Ext.grid.GridPanel({
            store : meteringRateStore,
            colModel : meteringRateCol,
            autoScroll : false,
            height : 650,
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

            //paging bar
        });

        $('#timeGrid').html(' ');

        meteringRateGrid.reconfigure(meteringRateStore, meteringRateCol);
        meteringRateGrid.render('timeGrid');

    }

    // 미터 아이디 리스트 Grid
    var meterIdStore;
    var meterIdCol;
    var meterIdGrid;
    var getMeterIdGrid = function(){
        var grWidth = $('#ReportGridDiv').width()*0.3;

        meterIdStore = new Ext.data.JsonStore({
            autoLoad : {params:{start: 0, limit: 20}},
            url: "${ctx}/gadget/report/getMeterNumberList.do",
            baseParams : {
                'supplierId' : supplierId,
            },
            root: 'gridData',
            totalProperty : 'totalCnt',
            idProperty : 'no',
            fields: [
                { name: 'no', type: 'string' },
                { name: 'meterMds', type: 'string' },
                { name: 'modelName', type: 'string' },
            ],
            listeners : {
                beforeload: function (store, options) {
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) - 1
                    });
                }
            }
        });

        meterIdCol = new Ext.grid.ColumnModel({
            columns: [
                {
                    header: "<fmt:message key='aimir.number'/>",
                    dataIndex: 'no',
                    renderer: numberControllfunction,

                },{
                    header: "<fmt:message key='aimir.meterid'/>",
                    dataIndex: 'meterMds',

                },{
                    header: "<fmt:message key='aimir.model'/>",
                    dataIndex: 'modelName',

                }
            ],
            defaults: {
                sortable : true,
                menuDisable : true,
                align : 'center'
            }
        });

        function numberControllfunction(value, me, record, rowNumber, columnIndex, store) {
            return Ext.util.Format.number(store.totalLength - value + 1, numberFormat);
        }


        meterIdGrid = new Ext.grid.GridPanel({
            store: meterIdStore,
            colModel: meterIdCol,
            selModel : new Ext.grid.RowSelectionModel({
                singleSelect : true,
                listeners : {
                    rowselect : function(selectionModel, columnIndex, value) {
                        var param = value.data;
                        // 검색 조건 Meter ID의 텍스트 박스 채우기
                        $('#meteridbox').val(param.meterMds.trim());
                    }
                }
            }),
            autoScroll: false,
            height: 520,
            width: grWidth,
            stripeRows: true,
            columnLines: true,
            loadMask: {
                msg: 'Loading...'
            },
            viewConfig: {
                forceFit:true,
                scrollOffset: 1,
                enableRowBody:true,
                showPreview:true,
                emptyText: '<fmt:message key="aimir.extjs.empty"/>',
            },
            bbar: new Ext.PagingToolbar({
                pageSize: 20,
                store: meterIdStore,
                displayInfo: true,
                displayMsg: ' {0} - {1} / {2}'
            })
        });

        $('#meterIdList').html(' ');
        meterIdGrid.reconfigure(meterIdStore, meterIdCol);
        meterIdGrid.render('meterIdList');

    }



</script>

<div id="wrapper" class="max"><div class="gadget_body">
    <div id="ReportGridDiv" class="width-auto margin-t10px">
<!-- Section 1. 미터 리스트 -->
        <span class="border_blu margin-r5 padding10px" >
            <label class="check"><fmt:message key="aimir.meterid"/></label>
            <div id="meterIdList" class="margin-t10px padding-t7px padding-left3px"></div>
        </span>
<!-- Section 2. 검색, 개요 -->
        <span class="border_blu margin-l5 padding10px" style="width:25%;" >
            <label class="check"><fmt:message key="aimir.button.search"/></label>
            <!-- 검색 조건 시작 -->
            <div class="searchoption-container margin-t10px">
                <table class="searchoption wfree">
                    <tr>
                        <td class="blue12pt padding-r10px"><fmt:message key="aimir.meterid"/></td>
                        <td class="padding-r10px"><span>
					        <input type="text" id="meteridbox" name="meteridbox" /></span>
                        </td>
                    </tr><tr>
                        <td class="blue12pt padding-r10px"><fmt:message key="aimir.date"/></td>
                        <td><input id="historyStartDate" class="day" type="text"></td>
                    </tr><tr>
                        <td class="padding-r10px"><span class="am_button">
					        <a href="javascript:meteringRateSearch()"><fmt:message key="aimir.button.search"/></a></span>
                        </td>
                        <td class="padding-r10px"><span class="am_button">
					        <a href="javascript:clearSearchOptions()"><fmt:message key="aimir.button.initialize"/></a></span>
                        </td>
                    </tr>
                </table>
            </div>
            <span id="historyStartDateHidden" style="visible:hidden;"></span>
            <div class="dashedline"></div>
            <!-- 검색 조건 끝 -->
            <div id="rateGrid" class="margin-t10px padding-t7px padding-left3px"></div>
        </span>


<!-- Section 3. 세부 리스트 -->
        <span class="border_blu margin-l5 padding10px" >
            <label class="check"><fmt:message key="aimir.view.detail"/></label>
            <div id="timeGrid" class="margin-t10px padding-t7px padding-left3px">
                <label class="graybold11pt"><fmt:message key="aimir.extjs.empty"/></label>
            </div>
        </span>
    </div>


</div></div>
</body>
</html>
