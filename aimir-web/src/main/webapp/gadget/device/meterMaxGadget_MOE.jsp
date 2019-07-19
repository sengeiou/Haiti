<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="PRAGMA" content="NO-CACHE" />
<meta http-equiv="Expires" content="0" />
<title>Meter MaxGadget</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery-ajaxQueue.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/googleMap.jsp"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" src="${ctx}/js/cluetip/jquery.cluetip.js"></script>
<script type="text/javascript" src="${ctx}/js/cluetip/jquery.bgiframe.min.js"></script>
<script type="text/javascript" src="${ctx}/js/cluetip/jquery.hoverIntent.js"></script>
<%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>

<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* Ext-Js Grid Header style 정의. */
        .x-grid3-hd-inner {
            text-align: center;
            font-weight: bold;
        }
</style>

<script type="text/javascript" charset="utf-8">
    var flexMeterLogGrid;

    var meterGridStore;
    var meterChartGrid;
    var meterChartGridStore;

    var supplierId = "${supplierId}";
    supplierId = "" == supplierId ? -1 : supplierId;
    var loginId = "";
    var protocolType = "";

    var fcPieChartDataXml;
    var fcPieChart;
    var fcLogChartDataXml;
    var fcLogChart;
    var fcMeasureChartDataXml;
    var fcMeasureChart;

    var frUpload;
    // meter Id 중복값 check.
    var meterRegMdsIdCheck = false;
    var isNumberCheck = false;
    var allGridData =   null;
    var numberFormat = "";

    var permitLocationId = "${permitLocationId}";  // location 제한
    // 알림창 '미터정보'
    var extJsTitle = '<fmt:message key="aimir.meter" />' + " "
            + '<fmt:message key="aimir.info" />';

    // 수정권한
    var ondemandAuth = "${ondemandAuth}";
    var editAuth = "${editAuth}";
    var cmdAuth = "${cmdAuth}";

    var preMeterId = '';
    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs = {
        hourly : 1,
        daily : 0,
        period : 1,
        weekly : 0,
        monthly : 1,
        monthlyPeriod : 0,
        weekDaily : 0,
        seasonal : 0,
        yearly : 0,
        search_hourly : 1,
        search_period : 1,
        search_monthly : 1,
        btn_hourly : 0,
        btn_daily : 0,
        btn_period : 1,
        btn_weekly : 0,
        btn_monthly : 1,
        btn_monthlyPeriod : 0,
        btn_weekDaily : 0,
        btn_seasonal : 0,
        btn_yearly : 0,
        btn_search_period : 1,
        btn_search_monthly : 1
    };

    // 탭명칭 변경시 값입력
    var tabNames = {
        hourly : '',
        daily : '',
        period : '',
        weekly : '',
        monthly : '',
        monthlyPeriod : '',
        weekDaily : '',
        seasonal : '',
        yearly : ''
    };


 	
    var purple = '#5E32BB'; //normal 
    var skyBlue = '#12ABBA'; //information
    var red = '#F31523'; //critical or unknown
    var orange = '#FC8F00'; // NA48h
    var gold = '#A99903'; // NA24h
    var blue = '#0718D7'; // A24h
    var setColorFrontTag = "<b style=\"color:";
    var setColorMiddleTag = ";\">"; 
    var setColorBackTag = "</b>";


    $.ajaxSetup({
        async : false
    });

    /**
     * 유저 세션 정보 가져오기
     */
    $.getJSON('${ctx}/common/getUserInfo.do', function(json) {
        if (json.supplierId != "") {
            loginId = json.loginId;
        }
    });

    $(function() {
        Ext.QuickTips.init();
        // Ext-JS Grid 컬럼사이즈 오류 수정
        Ext.override(Ext.grid.ColumnModel, {
            getColumnWidth : function(col) {
                var width = this.config[col].width;
                var stsize = 4;
                var chsize = stsize/this.config.length;

                if (typeof width != 'number') {
                    width = this.defaultWidth;
                }

                width = width - chsize;
                return width;
            }
        });

        // reset hidden field
        $("input[type=reset]").click(function(e) {
            $("input[type=hidden]").val('');
        });

        // Dialog
        $('#detail_dialog').dialog({
            autoOpen : false,
            resizable : false,
            modal : false
        });

        //meterSearchChart의 그리드부분
        renderChartGrid();

        //meterSearchGrid
        renderGrid();

        $(function() {
            $('#_general').bind('click', function(event) {
            });
        });

        $(function() {
            $('#_locationInfo').bind('click', function(event) {
                checkMeterId();
            });
        });

        $(function() {
            $('#_measurement').bind('click', function(event) {
                if (checkMeterId()) {
                    showMeasureChart();
                }
            });
        });

        $("#meterDetailTab").subtabs();

        // 위치정보와 meter id가 존재하면 구글을 통한 viw를제공

        $('#meterDetailTab').bind('tabsshow', function(event, ui) {
            if (ui.panel.id == "locationInfo")
                if (meterId != "")
                    viewMeterMap();
        });

        $('#sMeterType').selectbox();
        $('#sMeterGroup').selectbox();
        $('#sStatus').selectbox();
        $('#sModemYN').selectbox();
        $('#sCustomerYN').selectbox();
        $('#sModel').selectbox();

        // 검색결과 조건
        $('#sOrder').selectbox();
        $('#sCountperPage').selectbox();
        $('#sCommState').selectbox();

        frUpload = new AjaxUpload('fwUpload', {
            name : 'userfile',
            responseType : 'json',
            onSubmit : function(file, ext) {
                if($('#protocolType').val() == 'SMS') {
                    //파일 확장자 검색
                    if (!(ext && /^(dwl|DWL)$/.test(ext))){
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','is not dwl file');
                        return false;
                    }

                    this._settings.action = '${ctx}/gadget/device/command/smsFirmwareUpdate.do',
                    this._settings.data = {
                        loginId : loginId,
                        modemId : $('#modemId').val(),
                        ext : 'dwl'
                    };
                } else {
                    //파일 확장자 검색
                    if (!(ext && /^(bin|BIN)$/.test(ext))) {
                        Ext.Msg.alert('<fmt:message key='aimir.message'/>','is not bin file');
                        return false;
                    }

                    this._settings.action = '${ctx}/gadget/device/command/cmd-SX-Distribution.do',
                    this._settings.data = {
                        loginId : loginId,
                        meterId : meterId,
                        supplierId : supplierId,
                    };
                }
            },
            onComplete : function(file, response) {
                if (response.rtnStr != undefined)
                    ;
                $('#commandResult').val(
                        response.status + " : " + response.rtnStr);
            }
        });
    });

    //MeterSearchChart 그리드부분
    var meterChartGridOn = false;
    // paging 추가
    var renderChartGrid = function() {
        var width = $("#meterSearchChart").width()+10;
        var condition = getCondition();
        var searchChart = getSearchChart();
        var dataFild = searchChart[1];
        var pageSize = 5;

        meterChartGridStore  = new Ext.data.JsonStore({
            autoLoad   : {params:{start: 0, limit: pageSize}},
            url        : '${ctx}/gadget/device/getMeterSearchChart.do',
            baseParams : {
                sMeterType         : condition[0],
                sMdsId             : condition[1],
                sStatus            : condition[2],
                sMcuName           : condition[3],
                sLocationId        : condition[4],
                sConsumLocationId  : condition[5],
                sVendor            : condition[6],
                sModel             : condition[7],
                sInstallStartDate  : condition[8],
                sInstallEndDate    : condition[9],
                sModemYN           : condition[10],
                sCustomerYN        : condition[11],
                sLastcommStartDate : condition[12],
                sLastcommEndDate   : condition[13],
                sCommState         : condition[15],
                supplierId         : condition[16],
                sMeterGroup        : condition[17],
                sCustomerId        : condition[18],
                sCustomerName      : condition[19],
                sPermitLocationId  : condition[20],
                sMeterAddress      : condition[21]
            },
            root : 'gridData',
            totalProperty : 'totalCnt',
            listeners : {
                beforeload: function(store, options) {
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                    });
                }
            },
            fields : [dataFild[0],
                      dataFild[1],
                      dataFild[2],
                      dataFild[3],
                      dataFild[4],
                      dataFild[5]]
        });

        var colWidth = (width - 50)/5;
        meterChartGridColModel = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true,
                menuDisabled: true,
                width: colWidth
            },
            columns: [{
                header: "<fmt:message key='aimir.number'/>",
                dataIndex: 'no',
                align: 'center',
                width: 50,
                tooltip: "<fmt:message key='aimir.number'/>",
                sortable: true
                },{
                header: "<fmt:message key='aimir.mcuid'/>",
                dataIndex: 'mcuSysID',
                align: 'center',
                tooltip: "<fmt:message key='aimir.mcuid'/>",
                sortable: true
                },{
                header: "<fmt:message key='aimir.24within'/>",
                dataIndex: 'value0',
                align: 'right',
                tooltip: "<fmt:message key='aimir.commstateGreen'/>",
                sortable: true
                },{
                header: "<fmt:message key='aimir.24over'/>",
                dataIndex: 'value1',
                align: 'right',
                tooltip: "<fmt:message key='aimir.commstateYellow'/>",
                sortable: true
                },{
                header: "<fmt:message key='aimir.48over'/>",
                dataIndex: 'value2',
                align: 'right',
                tooltip: "<fmt:message key='aimir.commstateRed'/>",
                sortable: true
                },{
                header: "<fmt:message key='aimir.bems.facilityMgmt.unknown'/>",
                dataIndex: 'value3',
                align: 'right',
                tooltip: "<fmt:message key='aimir.bems.facilityMgmt.unknown'/>",
                sortable: true
            }]
        });

        if (!meterChartGridOn) {
            meterChartGrid = new Ext.grid.GridPanel({
                width : width,
                height : 172,
                store : meterChartGridStore,
                colModel : meterChartGridColModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, columnIndex, value) {
                            var param = value.data;
                            $('#sMcuName').val(param.mcuSysID);
                            $("#sCommState option:eq(0)").attr("selected", "selected");
                            $('#sCommState').selectbox();

                            $("#sOrder option:eq(0)").attr("selected", "selected");
                            $('#sOrder').selectbox();

                            renderGrid();
                        }
                    }
                }),
                autoScroll : false,
                stripeRows : true,
                columnLines: true,
                loadMask : {
                    msg : 'loading...'
                    },
                renderTo : 'meterSearchChart',
                viewConfig : {
                    showPreview : true,
                    emptyText : 'No data to display'
                },
                bbar : new Ext.PagingToolbar({
                    pageSize : pageSize,
                    store : meterChartGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            meterChartGridOn = true;
        } else {
            meterChartGrid.setWidth(width);
            meterChartGrid.reconfigure(meterChartGridStore, meterChartGridColModel);
            var bottomToolbar = meterChartGrid.getBottomToolbar();
            bottomToolbar.bindStore(meterChartGridStore);
        }
    };

    //meterSearchGrid
    var meterGridOn = false;
    var meterGrid;
    var renderGrid = function() {
        var width = $("#meterSearchGrid").width();
        var condition = getCondition();

        meterGridStore = new Ext.data.JsonStore({
            autoLoad : {params:{start: 0, limit: 10}},
            url : '${ctx}/gadget/device/getMeterSearchGrid.do',
            baseParams: {
                sMeterType         : condition[0],
                sMdsId             : condition[1],
                sStatus            : condition[2],
                sMcuName           : condition[3],
                sLocationId        : condition[4],
                sConsumLocationId  : condition[5],
                sVendor            : condition[6],
                sModel             : condition[7],
                sInstallStartDate  : condition[8],
                sInstallEndDate    : condition[9],
                sModemYN           : condition[10],
                sCustomerYN        : condition[11],
                sLastcommStartDate : condition[12],
                sLastcommEndDate   : condition[13],
                sOrder             : condition[14],
                sCommState         : condition[15],
                supplierId         : condition[16],
                sMeterGroup        : condition[17],
                sGroupOndemandYN   : 'N',
                sCustomerId        : condition[18],
                sCustomerName      : condition[19],
                sPermitLocationId  : condition[20],
                sMeterAddress      : condition[21]
            },
            root : 'gridData',
            totalProperty : 'totalCnt',
            idProperty : 'no',
            listeners : {
                beforeload: function(store, options){
                    Ext.apply(options.params, {
                        page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) - 1
                    });
                },load: function(store, record, options){
                    numberFoamat = store.reader.jsonData.mdNumberPattern;
                },
            },
            fields : [
                { name: 'no', type: 'string' },
                { name: 'commStatus', type: 'string' },
                { name: 'commStatusByCode', type: 'string' },
                { name: 'customer', type: 'string' },
                { name: 'installDate', type: 'string' },
                { name: 'lastCommDate', type: 'string' },
                { name: 'locName', type: 'string' },
                { name: 'mcuSysID', type: 'string' },
                { name: 'meterId', type: 'string' },
                { name: 'meterMds', type: 'string' },
                { name: 'meterType', type: 'string' },
                { name: 'meterMds', type: 'string' },
                { name: 'modelName', type: 'string' },
                { name: 'vendorName', type: 'string' },
                { name: 'contractNumber', type: 'string' },
                { name: 'modemModelName', type: 'string' },
                { name: 'customerId', type: 'string' },
                { name: 'customerName', type: 'string' },
                { name: 'installProperty', type: 'string' },
                { name: 'installId', type: 'string' },
                { name: 'address', type: 'string' },
                { name: 'meterAddress', type: 'string' },
                { name: 'mdNumberPattern', type: 'string' },
                { name: 'activityStatus', type: 'string'} 
            ]
        });

        meterGridColModel = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true,
                menuDisabled: true
            },
            columns: [{
                header: "<fmt:message key='aimir.number'/>",
                dataIndex: 'no',
                align:'center',
                width: 55,
                renderer: function(value, me, record, rowNumber, columnIndex, store) {
                    return Ext.util.Format.number(store.totalLength - value + 1, numberFormat);
                },
                sortable: true
            },{
                header: "<fmt:message key='aimir.meterid'/>",
                dataIndex: 'meterMds',
                align:'center',
                width: 85,
                sortable: true,
                renderer : function(value, me, record, rowNumber, columnIndex, store) {
                	
                    if(record.data.activityStatus == "A24h")
                    	return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
                    else if(record.data.activityStatus == "NA24h")
                    	return setColorFrontTag + gold + setColorMiddleTag + value + setColorBackTag;
                    else if(record.data.activityStatus == "NA48h")
                    	return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
                    else if(record.data.activityStatus == "unknown")
                    	return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
                    else
                    	return value;
                }
            },{
                header: "<fmt:message key='aimir.metertype'/>",
                dataIndex: 'meterType',
                align:'center',
                width: 85,
                sortable: true
            },{
                header: "<fmt:message key='aimir.buildingMgmt.contractNumber'/>",
                dataIndex: 'contractNumber',
                align:'center',
                width: 75,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key='aimir.customerid'/>",
                dataIndex: 'customerId',
                align:'center',
                width: 95,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key='aimir.customername'/>",
                dataIndex: 'customerName',
                align:'center',
                width: 110,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key='aimir.customeraddress'/>",
                dataIndex: 'address',
                align:'center',
                width: 95,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key='aimir.status'/>",
                dataIndex: 'commStatusByCode',
                align:'center',
                width: 100,
                sortable: true,
                renderer: function(val, me, record, rowNumber, columnIndex, store) {            
                    var fmtMessage = getFmtMessageCommAlert();
                    switch (val){
                        case "fmtMessage00": return fmtMessage[0]; break;
                        case "fmtMessage24": return fmtMessage[1]; break;
                        case "fmtMessage48": return fmtMessage[2]; break;
                        case Meterstatus.NEW_REGISTERED: return setColorFrontTag + skyBlue + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break;
                        case Meterstatus.NORMAL : return setColorFrontTag + purple + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break;
                        case Meterstatus.CUT_OFF : return setColorFrontTag + red + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break;
                        default : return record.data.commStatus;
                    }
                }
            },{
                header: "<fmt:message key='aimir.mcuid'/>",
                dataIndex: 'mcuSysID',
                align:'center',
                width: 75,
                sortable: true
            },{
                header: "<fmt:message key='aimir.vendor'/>",
                dataIndex: 'vendorName',
                align:'center',
                width: 75,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key='aimir.model'/>",
                dataIndex: 'modelName',
                align:'center',
                width: 90,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key='aimir.modem.model'/>",
                dataIndex: 'modemModelName',
                align:'center',
                width: 95,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key='aimir.lastcomm'/>",
                dataIndex: 'lastCommDate',
                align:'center',
                width: 125,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key='aimir.location'/>",
                dataIndex: 'locName',
                align:'center',
                width: 75,
                sortable: true,
                renderer : addTooltip
            },{
                header: "<fmt:message key="aimir.installProperty"/>",
                dataIndex: 'installProperty',
                align:'center',
                width: 95,
                sortable: true,
                renderer: addTooltip
            },{
                header: "<fmt:message key="aimir.install"/>"+"<fmt:message key="aimir.id"/>",
                dataIndex: 'installId',
                align:'center',
                width: 95,
                sortable: true,
                renderer: addTooltip
            },{
                header: "<fmt:message key='aimir.meter.address'/>",
                dataIndex: 'meterAddress',
                align:'center',
                width: 110,
                sortable: true,
                renderer : addTooltip
            }]
        });

        if (!meterGridOn) {
            meterGrid = new Ext.grid.GridPanel({
                layout : 'fit',
                width : width,
                height : 310,
                store : meterGridStore,
                colModel : meterGridColModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, columnIndex, value) {
                            var param = value.data;
                            setMeterId(param.meterId, param.meterMds, param.meterType);
                        }
                    }
                }),
                stripeRows : true,
                columnLines : true,
                loadMask : {
                    msg : 'loading...'
                },
                renderTo : 'meterSearchGrid',
                viewConfig : {
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                   //, enableTextSelection : true
                },
                bbar : new Ext.PagingToolbar({
                    pageSize : 10,
                    store : meterGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}'
                })
            });
            meterGridOn = true;
        } else {
            meterGrid.setWidth(width);
            var bottomToolbar = meterGrid.getBottomToolbar();
            meterGrid.reconfigure(meterGridStore, meterGridColModel);
            bottomToolbar.bindStore(meterGridStore);
        }
    };

    // grid column tooltip
    function addTooltip(value, metadata) {
        if (value != null && value != "") {
            metadata.attr = 'ext:qtip="' + value + '"';
        }
        return value;
    }

    //MeterMeteringGrid
    var MeteringGridOn = false;
    var renderMeteringGrid = function() {
        //var width = $("#meterMeteringGrid").width();
        var condition = getMeteringByMeterCondition();
        var unit = getUnitCondition(condition[1]);

        meteringGridStore = new Ext.data.JsonStore({
            autoLoad        : {params:{start: 0, limit: 10}},
            url             : '${ctx}/gadget/device/getMeteringDataByMeterGrid.do',
            baseParams: {
                meterId         :   condition[0],
                meterType       :   condition[1],
                searchStartDate :   condition[2],
                searchEndDate   :   condition[3],
                searchStartHour :   condition[4],
                searchEndHour   :   condition[5],
                searchDateType  :   condition[6],
                supplierId      :   condition[7],
            },
            root            : 'gridData',
            totalProperty   : 'totalCnt',
            idProperty      : 'No',
            listeners       : {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) - 1
                         });
                }
            },
            fields  : [
                    { name: 'No', type: 'string' },
                    { name: 'meteringDate', type: 'string' },
                    { name: 'usage', type: 'double' },
                    { name: 'co2', type: 'double' },
                ]
        });

        meteringGridColModel = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true,
                menuDisabled: true
            },
            columns: [{
                header: "<fmt:message key='aimir.number'/>",
                dataIndex: 'No',
                align:'right',
                width: 15,
                sortable: true
            },{
                header: "<fmt:message key='aimir.meteringtime'/>",
                dataIndex: 'meteringDate',
                align:'center',
                width: 35,
                sortable: true
            },{
                header: "<fmt:message key='aimir.usage'/>"+unit+"",
                dataIndex: 'usage',
                align:'right',
                width:25,
                sortable: true
            },{
                header: "<fmt:message key='aimir.co2formula'/>",
                dataIndex: 'co2',
                align:'right',
                width: 25,
                sortable: true
            }]
        });

        if (!MeteringGridOn) {
            meteringGrid = new Ext.grid.GridPanel({
               //width : width,
               height       : 290,
               store        : meteringGridStore,
               colModel     : meteringGridColModel,
               stripeRows   : true,
               columnLines  : true,
               loadMask     :{
                   msg: 'loading...'
               },
               renderTo     : 'meterMeteringGrid',
               viewConfig   : {
                   forceFit:true,
                   enableRowBody    :true,
                   showPreview      :true,
                   emptyText        : 'No data to display'
               },
               bbar : new Ext.PagingToolbar({
                   pageSize: 10,
                   store: meteringGridStore,
                   displayInfo: true,
                   displayMsg: ' {0} - {1} / {2}'
               })
           });
            MeteringGridOn = true;
        } else {
            var bottomToolbar = meteringGrid.getBottomToolbar();
            meteringGrid.reconfigure(meteringGridStore, meteringGridColModel);
            bottomToolbar.bindStore(meteringGridStore);
        }
    }

    $(document).ready(function() {
        // Ondemand 권한 체크
        if (ondemandAuth == "true") {
            $("#groupOndemandBtn").show();
            $("#onDemandButton").show();
        } else {
            $("#groupOndemandBtn").hide();
            $("#onDemandButton").hide();
        }

        if (editAuth == "true") {
            $("#updLocBtnList").show();
        } else {
            $("#updLocBtnList").hide();
        }

        if (ondemandAuth != "true" && cmdAuth != "true") {
            $("#meterCommand").hide();
        }

        var locDateFormat = "yymmdd";

        $("#sInstallStartDate").datepicker({
            maxDate : '+0m',
            showOn : 'button',
            buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
            buttonImageOnly : true,
            dateFormat : locDateFormat,
            onSelect : function(dateText, inst) {
                modifyDateLocal(dateText, inst);
            }
        });
        $("#sInstallEndDate").datepicker({
            maxDate : '+0m',
            showOn : 'button',
            buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
            buttonImageOnly : true,
            dateFormat : locDateFormat,
            onSelect : function(dateText, inst) {
                modifyDateLocal(dateText, inst);
            }
        });

        $("#sLastcommStartDate").datepicker({
            maxDate : '+0m',
            showOn : 'button',
            buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
            buttonImageOnly : true,
            dateFormat : locDateFormat,
            onSelect : function(dateText, inst) {
                modifyDateLocal(dateText, inst);
            }
        });
        $("#sLastcommEndDate").datepicker({
            maxDate : '+0m',
            showOn : 'button',
            buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
            buttonImageOnly : true,
            dateFormat : locDateFormat,
            onSelect : function(dateText, inst) {
                modifyDateLocal(dateText, inst);
            }
        });

        $("#onDemandFromDate").datepicker({
            maxDate : '+0m',
            showOn : 'button',
            buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
            buttonImageOnly : true,
            dateFormat : locDateFormat,
            onSelect : function(dateText, inst) {
                modifyDateLocal(dateText, inst);
            }
        });
        $("#onDemandToDate").datepicker({
            maxDate : '+0m',
            showOn : 'button',
            buttonImage : '${ctx}/themes/images/default/setting/calendar.gif',
            buttonImageOnly : true,
            dateFormat : locDateFormat,
            onSelect : function(dateText, inst) {
                modifyDateLocal(dateText, inst);
            }
        });

        // OnDemand 일자조건 좌우 화살표클릭 이벤트 생성
        $(function() { $('#onDemandFromDateLeft').bind('click', function(event) { onDemandDateArrow('onDemandFromDate', $('#onDemandFromDate').val(), -1); } ); });
        $(function() { $('#onDemandFromDateRight').bind('click', function(event) { onDemandDateArrow('onDemandFromDate', $('#onDemandFromDate').val(), 1); } ); });
        $(function() { $('#onDemandToDateLeft').bind('click', function(event) { onDemandDateArrow('onDemandToDate', $('#onDemandToDate').val(), -1); } ); });
        $(function() { $('#onDemandToDateRight').bind('click', function(event) { onDemandDateArrow('onDemandToDate', $('#onDemandToDate').val(), 1); } ); });

        initMeasureChartData();
        updateFChart();
        hide();
    });

    // 파일 업로드
    var genFileUploadTag = function(meterSerial, aTagId, url) {
        new AjaxUpload(aTagId, {
            action : url,
            data : {
                meterSerial : meterSerial,
                fileType : 'csv'
            },
            responseType : 'json',
            onSubmit : function(file, ext) {
                if (!(ext && /^(csv|CSV)$/.test(ext))) {
                    Ext.Msg.alert('<fmt:message key='aimir.message'/>','is not csv file');
                    return false;
                }
            },
            onComplete : function(file, response) {
                $('#commandResult').val(response.rtnStr);
            }
        });
    }

    // 검색조건의 날짜를 Local유형에서 일반 유형으로 변경
    function modifyDateLocal(setDate, inst) {
        var dateId = '#' + inst.id;
        var dateHiddenId = '#' + inst.id + 'Hidden';

        $(dateHiddenId).val($(dateId).val());

        $.getJSON("${ctx}/common/convertLocalDate.do", {
            dbDate : setDate,
            supplierId : supplierId
        }, function(json) {
            $(dateId).val(json.localDate);
        });
    }

    // meterId가 비었을때 선택불가
    function checkMeterId() {
        if (meterId == "") {
            Ext.MessageBox
                    .show({
                        title : '<fmt:message key='aimir.meter'/> <fmt:message key='aimir.info'/>',
                        buttons : Ext.MessageBox.OK,
                        msg : '<fmt:message key='aimir.nometer'/>',
                        icon : Ext.MessageBox.INFO
                    });
            return false;
        }
        return true;
    }

    var meterId = ''; // 선택된 meterId
    var meterType = ''; // 선택된 meterType
    var meterMds = ''; // 선택된 meterMds
    var logGrid = ''; // 선택된 logGrid

    function getMeterMds() {
        return meterMds;
    }
    function getMeterId() {
        return meterId;
    }

    // 미터 상세조회
    function setMeterId(gridMeterId, gridMeterMds, gridMeterType) {
        var tabs_selectIndex = $('#meterDetailTab').tabs().tabs('option',
                'selected');

        meterId = gridMeterId;
        meterMds = gridMeterMds;
        meterType = gridMeterType;
        getMeter();

        frUpload._settings.data.modemId = $('#modemId').val();
        frUpload._settings.data.meterId = meterId;

        viewMeterMap();
        showMeasureChart();
    }

    // commonDateTabButtonType2.jsp에서 검색
    function send2() {
        fcLogChartUpdate();
    }

    function getLogCondition() {
        var arrayObj = Array();

        arrayObj[0] = meterMds;
        arrayObj[1] = $('#btn_searchStartDate').val();
        arrayObj[2] = $('#btn_searchEndDate').val();
        arrayObj[3] = logGrid;

        return arrayObj;
    }

    // 현재 미터의 상태를 array에 추가함.
    // sModel이 현재 미터 model의 이름 <-SX2 에 대해서 firmware upgrade 버튼 생성
    function getCondition() {
        var arrayObj = Array();

        arrayObj[0] = $('#sMeterType').val();
        arrayObj[1] = $('#sMdsId').val();
        arrayObj[2] = $('#sStatus').val();

        arrayObj[3] = $('#sMcuName').val();
        arrayObj[4] = $('#sLocationId').val();
        arrayObj[5] = $('#sConsumLocationId').val();
        arrayObj[6] = $('#sVendor').val();
        arrayObj[7] = $('#sModel').val();

        arrayObj[8] = $('#sInstallStartDateHidden').val();
        arrayObj[9] = $('#sInstallEndDateHidden').val();

        arrayObj[10] = $('#sModemYN').val();
        arrayObj[11] = $('#sCustomerYN').val();
        arrayObj[12] = $('#sLastcommStartDateHidden').val();
        arrayObj[13] = $('#sLastcommEndDateHidden').val();
        arrayObj[14] = $('#sOrder').val();
        arrayObj[15] = $('#sCommState').val();

        arrayObj[16] = supplierId;

        arrayObj[17] = $('#sMeterGroup').val();

        arrayObj[18] = $('#sCustomerId').val();
        arrayObj[19] = $('#sCustomerName').val();

        arrayObj[20] = permitLocationId;
        arrayObj[21] = $('#sMeterAddress').val();

        return arrayObj;
    }

    function searchMeter() {
        if (Number($('#sInstallStartDateHidden').val()) > Number($(
                '#sInstallEndDateHidden').val())) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Error: Invalid <fmt:message key='aimir.installdate'/> value");
            return;
        }
        if (Number($('#sLastcommStartDateHidden').val()) > Number($(
                '#sLastcommEndDateHidden').val())) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"Error: Invalid <fmt:message key='aimir.lastcomm'/> value");
            return;
        }

        updateFChart();
        renderChartGrid();
        renderGrid();
    }

    // Group Ondemand Start
    var array = new Array;
    var meterArray = new Array;
    var imgWin;
    var imgWin2;
    var imgWin3;
    var gridOn = false;
    var grid;
    var store;
    var detail = new Array;
    var count;
    var gridH = 0;
    var winH = 0;
    var ajaxSuccessCount = 0; // ajaxQueue의 요청이 완료된 count
    var queueName = undefined;

    function getData(index) {
        var record = store.getAt(index);
        var temp = record.get('status');
        var noDataHtml = '<html><div  style=\"width: 180px;FONT-SIZE: 36px;  TEXT-ALIGN:center; padding: 50px 0px 50px 0px;\"> No Data!</div></html>';

        if (temp == 'Success') {

            //성공 윈도우
            if (!imgWin2) {
                imgWin2 = new Ext.Window({
                    title : 'Group onDemand DATA',
                    id : 'drAlertWinIdDataPop',
                    applyTo : 'drAlertDataPop',
                    autoScroll : true,
                    pageX : 100,
                    pageY : 50,
                    width : 800,
                    height : 700,
                    closeAction : 'hide',
                    html : detail[index]
                });
            } else {
                imgWin2.update(detail[index]);
            }
            Ext.getCmp('drAlertWinIdDataPop').show();
        } else {
            //실패 윈도우
            if (!imgWin3) {
                imgWin3 = new Ext.Window({
                    title : 'Group onDemand DATA',
                    id : 'drAlertDataPopFailure',
                    applyTo : 'drAlertDataPopFailure',
                    autoScroll : true,
                    pageX : 100,
                    pageY : 50,
                    width : 200,
                    height : 200,
                    closeAction : 'hide',
                    html : noDataHtml
                });
            } else {
                imgWin3.update(noDataHtml);
            }
            Ext.getCmp('drAlertDataPopFailure').show();
        }
    }

    function onDemandTask() {
        ajaxSuccessCount = 0;//요청 완료시 counting된다.
        var d = new Date();
        var time = d.getFullYear().toString() + (d.getMonth() + 1).toString()
                + d.getDate().toString() + '000000';

        //비동기 방식으로 요청한다.
        $.ajaxSetup({
            async : true
        });

        //처음 항목에 loading 이미지 추가
        store.getAt(0).set('status',
                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

        for (var i = 0; i < meterArray.length; i++) {

            //요청을 큐에 쌓아 순차적으로 처리하는 플러그인.
            queueName = $.ajaxQueue({
                type : "GET",
                url : '${ctx}/gadget/device/command/cmdOnDemand.do',
                data : {
                    'target' : meterArray[i][0],
                    'loginId' : loginId,
                    'fromDate' : time,
                    'toDate' : time
                },
                success : function(returnData) {
                    var i = ajaxSuccessCount;
                    detail[i] = returnData.detail;
                    grid.getView().focusRow(i);
                    var record = store.getAt(i);
                    if (returnData.rtnStr == 'java.lang.NullPointerException') {
                        record.set('status', 'Not Found Meter!');
                    } else if (returnData.rtnStr == 'Success') {
                        record.set('status', returnData.rtnStr);
                        record.set('view',
                                "<a href='#' onclick='getData(" + i + ");' class='btn_blue'><span><fmt:message key='aimir.report.mgmt.view'/></span></a>");
                    } else if (returnData.rtnStr == '') {
                        record.set('status', 'Failure');
                    } else {
                        record.set('status', returnData.rtnStr);
                    }
                    ajaxSuccessCount++;
                    if (meterArray.length != ajaxSuccessCount)
                        store.getAt(ajaxSuccessCount).set('status',
                                'Processing...     <img src="${ctx}/themes/images/access/grid/loading.gif" align="middle"></img>');

                    if (window.ajaxQueueCount[queueName] == 1) {// 맨 마지막 동작일때
                        $.ajaxSetup({
                            async : true
                        });
                    }
                }
            });
        }
    }

    //그룹검침 시작 메소드
    function groupOndemandService() {

        $.ajaxSetup({
            cache : false
        });
        //해당 그룹 미터정보 얻기
        meterArray = groupOnDemand();
        //그리드, 윈도우 높이 구하기
        this.gridH = 100 + Number(meterArray.length * 25);
        this.winH = 200 + Number(meterArray.length * 25);
        if (this.gridH > 600)
            this.gridH = 600;
        if (this.winH > 700)
            this.winH = 700;

        //그리드 데이터 생성
        if (meterArray != null) {
            count = meterArray.length;
            for (var i = 0; i < count; i++) {
                var gridData = meterArray[i][1];
                var arrayData = [gridData, 'Processing...'];

                array[i] = arrayData;
            }

            makeAlertWindow();
        } else {
            if (imgWin != undefined) {
                Ext.getCmp('drAlertWinId').hide();
            }
        }

        $.ajaxSetup({
            cache : true
        });
    }

    //그룹 검침
    function groupOnDemand(){
        var meterIdList = new Array();
        var condition = getCondition();

        var params = {
            sMeterType          : condition[0],
            sMdsId              : condition[1],
            sStatus             : condition[2],
            sMcuName            : condition[3],
            sLocationId         : condition[4],
            sConsumLocationId   : condition[5],
            sVendor             : condition[6],
            sModel              : condition[7],
            sInstallStartDate   : condition[8],
            sInstallEndDate     : condition[9],
            sModemYN            : condition[10],
            sCustomerYN         : condition[11],
            sLastcommStartDate  : condition[12],
            sLastcommEndDate    : condition[13],
            curPage             : 0,
            sOrder              : condition[14],
            sCommState          : condition[15],
            supplierId          : condition[16],
            sMeterGroup         : condition[17],
            sGroupOndemandYN    : "Y",
            sPermitLocationId   : condition[20],
            sMeterAddress       : condition[21]
        };

        var jsonText = $.ajax({
            type: "POST",
            url: "${ctx}/gadget/device/getMeterSearchGrid.do",
            data: params,
            async: false
        }).responseText;

        eval("result=" + jsonText);

        allGridData = result.allGridData;

        if (allGridData.length > 0) {
            for (var i = 0 ; i < allGridData.length; i++) {
                meterIdList[i] = [allGridData[i]["meterId"],allGridData[i]["meterMds"]];
            }
        }

        return meterIdList;
    }

    //팝업 윈도우 생성
    function makeAlertWindow() {
        if (store == undefined) {
            store = new Ext.data.ArrayStore({
                fields : [ {
                    name : 'meterMds'
                }, {
                    name : 'status'
                } ]
            });
        }
        store.loadData(array);

        var colModel = new Ext.grid.ColumnModel({
            defaults : {
                width : 100,
                height : 100,
                sortable : true
            },
            columns : [{
                id : "meterMds",
                width : 150,
                header : "Meter ID",
                dataIndex : "meterMds"
            }, {
                header : "Status",
                width : 150,
                dataIndex : "status"
            }, {
                header : "Result",
                width : 70,
                dataIndex : "view"
            } ]
        });

        if (gridOn == false) {
            grid = new Ext.grid.GridPanel({
                height : gridH,
                store : store,
                colModel : colModel,
                width : 374
            });

            gridOn = true;
        } else {
            grid.reconfigure(store, colModel);
        }

        if (!imgWin) {
            imgWin = new Ext.Window({
                title : 'Group OnDemand Window',
                id : 'drAlertWinId',
                applyTo : 'drAlert',
                autoScroll : true,
                autoHeight : true,
                pageX : 400,
                pageY : 130,
                width : 389,
                height : winH,
                items : grid,
                closeAction : 'hide',
                onHide : function() {
                    if (queueName == undefined)
                        return;

                    $.ajaxQueueStop(queueName);
                }
            });
        } else {
            imgWin.setHeight(winH);
            grid.setHeight(gridH);
        }
        Ext.getCmp('drAlertWinId').show();

        array = new Array;
        setTimeout("onDemandTask();", 100);
    }
    // Group Ondemand End

    function viewLogGrid(viewlogGrid) {
        logGrid = viewlogGrid;
        flexMeterLogGrid.requestSend();
    }

    function getSearchChart() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key='aimir.number'/>"; // Grid Title
        fmtMessage[1] = "<fmt:message key='aimir.mcuid'/>"; // Grid Title

        fmtMessage[2] = "<fmt:message key='aimir.24within'/>"; // Pie Title
        fmtMessage[3] = "<fmt:message key='aimir.24over'/>"; // Pie Title
        fmtMessage[4] = "<fmt:message key='aimir.48over'/>"; // Pie Title

        var dataFild = new Array();
        dataFild[0] = "no";
        dataFild[1] = "mcuSysID";
        dataFild[2] = "value0";
        dataFild[3] = "value1";
        dataFild[4] = "value2";
        dataFild[5] = "value3";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "left";
        gridAlign[2] = "right";
        gridAlign[3] = "right";
        gridAlign[4] = "right";

        var gridWidth = new Array();
        gridWidth[0] = "500 ";
        gridWidth[1] = "1500";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";
        gridWidth[4] = "1000";

        var dataGrid = new Array();
        dataGrid[0] = fmtMessage;
        dataGrid[1] = dataFild;
        dataGrid[2] = gridAlign;
        dataGrid[3] = gridWidth;

        return dataGrid;
    }
    function getFmtMessageCommAlert() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.commstateGreen"/>";
        fmtMessage[1] = "<fmt:message key="aimir.commstateYellow"/>";
        fmtMessage[2] = "<fmt:message key="aimir.commstateRed"/>";

        return fmtMessage;
    }

    function getSearchGridColumn() {

        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.meterid"/>";
        fmtMessage[2] = "<fmt:message key="aimir.metertype"/>";
        fmtMessage[3] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[4] = "<fmt:message key="aimir.vendor"/>";

        fmtMessage[5] = "<fmt:message key="aimir.model"/>";
        fmtMessage[6] = "<fmt:message key="aimir.customer"/>";
        fmtMessage[7] = "<fmt:message key="aimir.lastcomm"/>";
        fmtMessage[8] = "<fmt:message key="aimir.location"/>";

        fmtMessage[9] = "<fmt:message key="aimir.status"/>";

        var dataFild = new Array();
        dataFild[0] = "no";
        dataFild[1] = "meterMds";
        dataFild[2] = "meterType";
        dataFild[3] = "mcuSysID";
        dataFild[4] = "vendorName";

        dataFild[5] = "modelName";
        dataFild[6] = "customer";
        dataFild[7] = "lastCommDate";
        dataFild[8] = "locName";

        dataFild[9] = "commStatus";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";
        gridAlign[4] = "center";

        gridAlign[5] = "center";
        gridAlign[6] = "center";
        gridAlign[7] = "center";
        gridAlign[8] = "center";

        gridAlign[9] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "500";
        gridWidth[1] = "1600";
        gridWidth[2] = "1200";
        gridWidth[3] = "1000";
        gridWidth[4] = "900";
        gridWidth[5] = "900";
        gridWidth[6] = "1000";
        gridWidth[7] = "1900";
        gridWidth[8] = "1200";
        gridWidth[9] = "900";

        var dataGrid = new Array();
        dataGrid[0] = fmtMessage;
        dataGrid[1] = dataFild;
        dataGrid[2] = gridAlign;
        dataGrid[3] = gridWidth;

        return dataGrid;
    }

    function getLogChart() {
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.commlog"/>";
        fmtMessage[1] = "<fmt:message key="aimir.changehistory"/>";
        fmtMessage[2] = "<fmt:message key="aimir.alerthistory"/>";
        fmtMessage[3] = "<fmt:message key="aimir.view.operationlog"/>";

        var dataFild = new Array();
        dataFild[0] = "commLog";
        dataFild[1] = "updateLog";
        dataFild[2] = "brokenLog";
        dataFild[3] = "operationLog";

        var chartColumn = new Array();
        chartColumn[0] = fmtMessage;
        chartColumn[1] = dataFild;

        return chartColumn;
    }

    function getLogGridComm() {
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.receiveTime"/>";
        fmtMessage[2] = "<fmt:message key="aimir.datatype"/>";
        fmtMessage[3] = "<fmt:message key="aimir.status"/>";

        var dataFild = new Array();
        dataFild[0] = "no";
        dataFild[1] = "receiveTime";
        dataFild[2] = "dataType";
        dataFild[3] = "status";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";

        var gridColumn = new Array();
        gridColumn[0] = fmtMessage;
        gridColumn[1] = dataFild;
        gridColumn[2] = gridAlign;
        gridColumn[3] = gridWidth;

        return gridColumn;
    }

    function getLogGridUpdate() {
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.time"/>";
        fmtMessage[2] = "<fmt:message key="aimir.operator"/>";
        fmtMessage[3] = "<fmt:message key="aimir.attribute"/>";
        fmtMessage[4] = "<fmt:message key="aimir.currentvalue"/>";
        fmtMessage[5] = "<fmt:message key="aimir.beforevalue"/>";

        var dataFild = new Array();
        dataFild[0] = "number";
        dataFild[1] = "time";
        dataFild[2] = "operator";
        dataFild[3] = "attribute";
        dataFild[4] = "currentvalue";
        dataFild[5] = "beforevalue";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";
        gridAlign[4] = "center";
        gridAlign[5] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";
        gridWidth[4] = "1000";
        gridWidth[5] = "1000";

        var gridColumn = new Array();
        gridColumn[0] = fmtMessage;
        gridColumn[1] = dataFild;
        gridColumn[2] = gridAlign;
        gridColumn[3] = gridWidth;

        return gridColumn;
    }

    function getLogGridBroken() {
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.majorValue"/>";
        fmtMessage[1] = "<fmt:message key="aimir.number"/>";
        fmtMessage[2] = "<fmt:message key="aimir.message"/>";
        fmtMessage[3] = "<fmt:message key="aimir.address"/>";
        fmtMessage[4] = "<fmt:message key="aimir.location"/>";
        fmtMessage[5] = "<fmt:message key="aimir.gmptime"/>";
        fmtMessage[6] = "<fmt:message key="aimir.closetime"/>";
        fmtMessage[7] = "<fmt:message key="aimir.duration"/>";

        var dataFild = new Array();
        dataFild[0] = "majorValue";
        dataFild[1] = "number";
        dataFild[2] = "message";
        dataFild[3] = "address";
        dataFild[4] = "location";
        dataFild[5] = "gmptime";
        dataFild[6] = "closetime";
        dataFild[7] = "duration";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";
        gridAlign[4] = "center";
        gridAlign[5] = "center";
        gridAlign[6] = "center";
        gridAlign[7] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";
        gridWidth[4] = "1000";
        gridWidth[5] = "1000";
        gridWidth[6] = "1000";
        gridWidth[7] = "1000";

        var gridColumn = new Array();
        gridColumn[0] = fmtMessage;
        gridColumn[1] = dataFild;
        gridColumn[2] = gridAlign;
        gridColumn[3] = gridWidth;

        return gridColumn;
    }

    function getLogGridCommand() {
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.time"/>";
        fmtMessage[2] = "<fmt:message key="aimir.targetType"/>";
        fmtMessage[3] = "<fmt:message key="aimir.operator"/>";
        fmtMessage[4] = "<fmt:message key="aimir.board.running"/>";
        fmtMessage[5] = "<fmt:message key="aimir.status"/>";
        fmtMessage[6] = "<fmt:message key="aimir.description"/>";

        var dataFild = new Array();
        dataFild[0] = "no";
        dataFild[1] = "logTime";
        dataFild[2] = "targetType";
        dataFild[3] = "operator";
        dataFild[4] = "operatorType";
        dataFild[5] = "status";
        dataFild[6] = "description";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "center";
        gridAlign[3] = "center";
        gridAlign[4] = "center";
        gridAlign[5] = "center";
        gridAlign[6] = "center";

        var gridWidth = new Array();
        gridWidth[0] = "1000";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";
        gridWidth[4] = "1000";
        gridWidth[5] = "1000";
        gridWidth[6] = "1000";

        var gridColumn = new Array();
        gridColumn[0] = fmtMessage;
        gridColumn[1] = dataFild;
        gridColumn[2] = gridAlign;
        gridColumn[3] = gridWidth;

        return gridColumn;
    }

    function doubleCheck(d) {
        if (d.length > 0) {
            var pattern = /^\d+\.?\d*$/;
            var str = d;
            if (str.match(pattern) == null)
                return false;
        }
        return true;
    }

    function nullCheck(list) {
        if (list == null)
            return false;

        for ( var i = 0; i < list.length; i++) {
            var item = list[i];
            if (item.val() == null || item.val() == ''
                    || item.val().trim() == '-') {
                item.select();
                var select = item.parent().find('div[class=selectbox-wrapper]');
                if (select != null) {
                    select.show();
                }
                return false;
            }
        }
        return true;
    }

    // 미터 기본 등록
    var insertMeterInfo = function() {
        //null 채크할 목록들
        var checkList = new Array();
        checkList.push($('#mdsId'));
        checkList.push($('#meterType_input'));
        checkList.push($('#deviceVendor_input'));
        checkList.push($('#modelId_input'));
        checkList.push($('#searchWord_3'));

        if (!nullCheck(checkList))
            return;

        if (!doubleCheck($('#usageThreshold').val())) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.threshold.number"/>');
            return;
        }

        if (!meterRegMdsIdCheck || preMeterId != $('#mdsId').val()) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.chkDuplicateId"/>'); //아이디 중복체크를 해야 합니다.
            return;

        } else {
            // select의 값을 hidden변수에 값으로 복사함
            $("#meterInfoFormEdit :input[id='meterTypeHidden']").val(
                    $('#meterType').val());
            $("#meterInfoFormEdit :input[id='deviceVendorHidden']").val(
                    $('#deviceVendor').val());
            $("#meterInfoFormEdit :input[id='modelIdHidden']").val(
                    $('#modelId').val());
            $("#meterInfoFormEdit :input[id='swVersionHidden']").val(
                    $('#swVersion').val());
            $("#meterInfoFormEdit :input[id='hwVersionHidden']").val(
                    $('#hwVersion').val());

            $("#meterInfoFormEdit :input[id='supplierId']").val(supplierId);

            // 미터 유형추가
            var meterType = $('#meterType').val();
            var params = "";

            var option = MeterTypeMap[meterType];

            if (option) {
                var url = '${ctx}/gadget/device/insert' + option.displayName
                        + '.do';
                var params = "";
                params = {
                    success : insertMeterInfoResult,
                    url : url,
                    type : 'post',
                    datatype : 'application/json'
                };

                $('#meterInfoFormEdit').ajaxSubmit(params);
            }
        }
    };

    // 미터 기본 등록 후처리
    function insertMeterInfoResult(responseText, status) {

        // 미터 조회
        var meterId = responseText.id;
        var meterMds = $('#mdsIdHidden').val();
        var meterType = $('#meterType option:selected').text();

		if(responseText.status == 'failed') {
			var tempMsg = '<fmt:message key="aimir.msg.insertfail" />';
	        Ext.Msg.alert(extJsTitle, $('#mdsId').val() + " "
	                + tempMsg.substring(tempMsg.lastIndexOf("_") + 1), null, null);
			
		} else {
	        var tempMsg = '<fmt:message key="aimir.msg.insertsuccess" />';
	        Ext.Msg.alert(extJsTitle, $('#mdsId').val() + " "
	                + tempMsg.substring(tempMsg.lastIndexOf("_") + 1), null, null);
		}
        // 목록 재조회
        meterGridStore.reload(meterGridStore.lasgOptions);

        // 미터 상세정보 초기화
        initMeterDetail();
    }

    // 미터 설치 현황 변경 result
    function updateMeterInstallInfoResult(responseText, status) {
        var tempMsg = '<fmt:message key="aimir.msg.updatesuccess" />';
        Ext.Msg.alert(extJsTitle, $('#mdsId').val() + " "
                + tempMsg.substring(tempMsg.lastIndexOf("_") + 1), null, null);

        // 미터그리드 재 조회
        meterGridStore.reload(meterGridStore.lastOptions);
        // 미터 재조회
        getMeter();
    }

    // 미터 기본 변경 처리
    var updateMeterInfo = function() {
        //null 채크할 목록들
        var checkList = new Array();
        checkList.push($('#mdsId'));
        checkList.push($('#meterType_input'));
        checkList.push($('#deviceVendor_input'));
        checkList.push($('#modelId_input'));
        checkList.push($('#searchWord_3'));

        if (!nullCheck(checkList))
            return;

        // select의 값을 hidden변수에 값으로 복사함
        $("#meterInfoFormEdit :input[id='meterId']").val(meterId);
        $("#meterInfoFormEdit :input[id='meterTypeHidden']").val(
                $('#meterType').val());
        $("#meterInfoFormEdit :input[id='deviceVendorHidden']").val(
                $('#deviceVendor').val());
        $("#meterInfoFormEdit :input[id='modelIdHidden']").val(
                $('#modelId').val());
        $("#meterInfoFormEdit :input[id='swVersionHidden']").val(
                $('#swVersion').val());
        $("#meterInfoFormEdit :input[id='hwVersionHidden']").val(
                $('#hwVersion').val());
        $("#meterInfoFormEdit :input[id='supplierId']").val(supplierId);

        if (!doubleCheck($('#usageThreshold').val())) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.threshold.number"/>');
            return;
        }

        // 미터 기본 변경
        var meterType = $('#meterType').val();
        var option = MeterTypeMap[meterType];

        var meterurl = '${ctx}/gadget/device/updateMeter.do';

        if(option.displayName == "WaterMeter"){
            meterurl = '${ctx}/gadget/device/updateWaterMeterInfo.do';
        }

        $.ajax({
            type : "POST",
            data : {
            	'id' : getMeterId(),
            	'mdsId' : getMeterMds(),
                'model.deviceVendor.id' : $('#deviceVendorHidden').val(),
                'model.id' : $('#modelIdHidden').val(),//추가
                'location.id' : $('#infolocationId').val(),
                'swVersion' : $('#swVersionHidden').val(),
                'hwVersion' : $('#hwVersionHidden').val(),
                'usageThreshold' : $('#usageThreshold').val(),
                'installDate' : $('#installDate').val()
            },
            dataType : "json",
            url : meterurl,
            success : updateMeterInfoResult,
            error : function(request, status) {
            	Ext.Msg.alert(extJsTitle,"meter update ajax comm failed", null, null);
            }
        });

    };

    // 미터 기본 변경 후처리
    function updateMeterInfoResult(responseText, status) {
        var tempMsg;
        if (responseText.id != undefined) {
            tempMsg = '<fmt:message key="aimir.msg.updatesuccess" />';
        } else {
            tempMsg = '<fmt:message key="aimir.msg.updatefail" />';
        }
        Ext.Msg.alert(extJsTitle, $('#mdsId').val() + " "
                + tempMsg.substring(tempMsg.lastIndexOf("_") + 1), null, null);

        // 목록 재조회
        reloadMeter();
        // 등록됨 미터 상세 조회
        setMeterId(meterId, meterMds, meterType);
    }

    // grid 의 store 만 reload
    function reloadMeter() {
        updateFChart();
        meterChartGridStore.reload(meterChartGridStore.lasgOptions);
        meterGridStore.reload(meterGridStore.lasgOptions);
    }

    // 미터 기본 삭제 처리
    var deleteMeterInfo = function() {
    	Ext.Msg.confirm('<fmt:message key="aimir.warning"/>', '<fmt:message key="aimir.msg.wantdelete" />', 
    			function(btn,text){
    		if(btn == 'yes'){    			
    			$("#meterInfoFormEdit :input[id='meterId']").val(meterId);

    	        $.ajax({
    	            type : "POST",
    	            data : {
    	            	'id' : getMeterId()
    	            },
    	            dataType : "json",
    	            url : '${ctx}/gadget/device/delteMeter.do',
    	            success : deleteMeterInfoResult,
    	            error : function(request, status) {
    	                Ext.Msg.alert("meter delete ajax comm failed");
    	            }
    	        });
    		}else {
    			Ext.Msg.alert('<fmt:message key="aimir.warning"/>','<fmt:message key="aimir.canceled" />');
    		}
    	});
    	
        
    };

    // 미터 삭제 후 처리
    function deleteMeterInfoResult(responseText, status) {
        if(responseText.id == null) {
            var tempMsg = '<fmt:message key="aimir.msg.deleteFail" />';
            Ext.Msg.alert(extJsTitle, $('#mdsId').val() + ": "
                    + tempMsg.substring(tempMsg.lastIndexOf("_") + 1), null, null);
        } else {
            var tempMsg = ':Meter <fmt:message key="aimir.msg.deletesuccess" />';
            Ext.Msg.alert(extJsTitle, $('#mdsId').val() + " "
                    + tempMsg.substring(tempMsg.lastIndexOf("_") + 1), null, null);
             // 미터 검색 재 조회
            searchMeter();

            // 미터 상세정보 초기화
            initMeterDetail();
        }
    }

    // 미터 상세 정보 초기화
    function initMeterDetail() {
        // js 변수 초기화
        var meterId = '';
        var meterType = '';
        var meterMds = '';
        var logGrid = '';

        // 기본정보 초기화
        $("#meterInfoDiv").load("${ctx}/gadget/device/meterMaxGadgetInfo.do");

        // 설치현황 초기화
        $("#meterInstallDiv").load(
                "${ctx}/gadget/device/meterMaxGadgetEmptyMeter.do");

        // 미터정보 초기화

        // 위치정보 초기화
        $('#meterLocForm').each(function() {
            this.reset();
        });

        // 상세>일반 Tab 클릭
        $("#meterDetailTab").tabs().tabs('select', 0);
    }

    // 장비기본정보 변경
    function changeMeterInfo(state) {
        if (state == "insert") {

            $('#meterInfoFormEdit').each(function() {
                this.reset();
            });
            $('#installDate').val("");
            $('#locationView').val("");
            $('#infolocationId').val("");
            $('#searchWord_3').val("");

            // 설치현황 초기화
            $("#meterInstallDiv").load("${ctx}/gadget/device/meterMaxGadgetEmptyMeter.do");

            $('#installDate').val("");
        } else if (state == "cancel") {
            getMeter();
            return;
        } else if (state == "edit") {
            getInputValues();

        } else {
            getInputValues();
        }

        // div 제어
        $('#meterDefaultInfoView').hide();
        $('#meterDefaultInfoEdit').hide();

        if (state == "insert") {
            $('#meterRegistarionView').show();
            $('#meterUpdateView').hide();
            $('#meterDefaultInfoEdit').show();
        } else if (state == "view") {
            $('#meterDefaultInfoView').show();
            $('#meterRegistarionView').hide();
            $('#meterUpdateView').hide();
        } else if (state == "edit") {
            $('#meterRegistarionView').hide();
            $('#meterUpdateView').show();
            $('#meterDefaultInfoEdit').show();
        }
        // 버튼제어
        $('#meterDefaultInfoInsertButton').hide();
        $('#meterDefaultInfoViewButton').hide();
        $('#meterDefaultInfoEditButton').hide();

        if (state == "insert")
            $('#meterDefaultInfoInsertButton').show();
        else if (state == "view")
            $('#meterDefaultInfoViewButton').show();
        else if (state == "edit")
            $('#meterDefaultInfoEditButton').show();

        var modelId = $('#modelIdHidden').val();

        // 미터 타입에 따른 온디멘드 버튼 표시 여부 초기화
        $('#relayControlButton').hide();
        $('#relayActivateButton').hide();
        $('#meterTimeSyncButton').hide();

        // 명령 버튼의 사용 가능 여부 판단.
        if (modelId != null && modelId != "") {
            enableButton(modelId);
        }
    }

    // 미터 모델에 따라 표시될 버튼을 결정한다.
    var enableButton = function(modelId) {
		//   fat를 위한 임시코드
		//   fat를 위한 임시코드
		//   fat를 위한 임시코드
		//   fat를 위한 임시코드
		//   GPRS모뎀하고 맵핑되어있는 LSIQ-3PCV미터 인경우 Ondemandbutton등 command버튼 보이지 않도록 처리
		//   devicemodel id = 14
        if(modelId == 14 && $('#supplierName').val() == 'MOE'){
            $("#onDemandButton").hide();
        }else{
            $("#onDemandButton").show();
        }

		//alert('supplierId =>> ' + supplierId + " name =>> " + supplierName + " name2 ==> " + $('#supplierName').val());

        //result 창 초기화
        $('#commandResult').val('');

        //버튼 초기화
        $('#relayControlButton').hide();
        $('#relayActivateButtion').hide();
        $('#meterTimeSyncButton').hide();
        $('#valveControlButton').hide();
        $('#btnTOUCalendar').hide();
        $('#btnDisplayItemSetting').hide();
        $('#demandResetButton').hide();
        $('#energyLevel').hide();
        $('#firmWareUpdateButton').hide();
        $('#meterEvent').hide();
        $('#billing').hide();
        $('#restoreDefaultFirmwareButton').hide();
        $('#pingButton').hide();
    	$('#tracerouteButton').hide();

        // Command 권한 체크
        if (cmdAuth != "true") {
            return;
        }
        
        //DeviceModelController.java
        $.getJSON('${ctx}/gadget/system/modelinfo.do', {
            devicemodelId : modelId,
            supplierId : supplierId
        }, function(json) {
        	
            if (json.namesOfContain.length > 0) {
            	// CommonConstants.java에 등록되어 있는 명령어
                // 해당 모델이 사용 가능한 명령어 목록을 읽어와 버튼을 표시한다. 모델목록은 CommonConstants.java Enum by EnableCommandModel 에 정의 되어있다.
                
                for ( var i = 0; i < json.namesOfContain.length; i++) {
                    switch (json.namesOfContain[i]) {
                        case 'relayControl':
                            $('#relayControlButton').show();
                            break;
                        case 'relayActivate':
                            $('#relayActivateButton').show();
                            break;
                        case 'timeSync':
							//   fat를 위한 임시코드
							//   fat를 위한 임시코드
							//   fat를 위한 임시코드
							//   fat를 위한 임시코드
							//   fat를 위한 임시코드
							//   GPRS모뎀하고 맵핑되어있는 LSIQ-3PCV미터 인경우 Ondemandbutton등 command버튼 보이지 않도록 처리
							//   devicemodel id = 14
					        if(modelId == 14 && $('#supplierName').val() == 'MOE'){
					            $("#meterTimeSyncButton").hide();
					        }else{
					            $("#meterTimeSyncButton").show();
					        }
                            //$('#meterTimeSyncButton').show();
                            break;
                        case 'valveControl':
                            $('#valveControlButton').show();
                            break;
                        case 'TOUCalendar':
                            $('#btnTOUCalendar').show();
                            var meterSerialNumber = $('#mdsIdHidden').val();
                            break;
                        case 'DisplayItemSetting':
                            $('#btnDisplayItemSetting').show();
                            var meterSerialNumber = $('#mdsIdHidden').val();
                            break;
                        case 'DemandReset':
                            $('#demandResetButton').show();
                            break;
                        case 'EnergyLevel':
                            $('#energyLevel').show();
                            break;
                        case 'FirmwareUpdate':
                            $('#firmWareUpdateButton').show();
                            break;
                        case 'MeterEvent':
                            $('#meterEvent').show();
                            break;
                        case 'Billing':
                            $('#billing').show();
                            break;
                        case 'RestoreDefaultFW':
                            $('#restoreDefaultFirmwareButton').show();
                            break;
                        case 'pingControl':
                            $('#pingButton').show();
                            break;
                        case 'TracerouteControl':
                            $('#tracerouteButton').show();
                            break;
                    }
                }
            }
        });
    };

    // 미터 설치 현황 변경
    var updateMeterInstallInfo = function() {
        $("#meterInstallFormEdit :input[id='meterId']").val(meterId);

        if(isNumberCheck == "false"){
            return;
        }

        var meterType = $('#meterType').val();
        var option = MeterTypeMap[meterType];

        if (option) {
            var url = '${ctx}/gadget/device/update' + option.displayName + '.do';
            var params = {
                success : updateMeterInstallInfoResult,
                url : url,
                type : 'post',
                datatype : 'application/json'
            };

            $('#meterInstallFormEdit').ajaxSubmit(params);
        }
    };

    // 미터 상세조회
    var getMeter = function() {
        // 미터 기본정보 조회
        var params = {
            "meterId" : meterId
        };
        $("#meterInfoDiv").load("${ctx}/gadget/device/getMeterInfo.do", params);

        protocolType = $('#protocolType').val();

        // 장비설치현황조회
        params = {
            "meterId" : meterId,
            "meterType" : meterType
        };

        $("#meterInstallDiv").load("${ctx}/gadget/device/getMeterByType.do",
                params);
    };

    // 미터 등록  - 미터아이디 중복확인

    function singleRegMeterIsMeterDuplicate() {
    	var trimMdsId = $.trim($('#mdsId').val());
    	$('#mdsId').val(trimMdsId);
    	preMeterId = trimMdsId;
        if ($('#mdsId').val() == null || $('#mdsId').val() == "") {
            Ext.Msg.alert(extJsTitle,
                    "<fmt:message key='aimir.inputMeterid'/>", null, null);
            $('#mdsId').focus();
            mcuRegSysIdCheck = false;
        } else {
            $.getJSON('${ctx}/gadget/device/isMeterDuplicateByMdsId.do', {
                'mdsId' : $('#mdsId').val()
            }, function(returnData) {
                if (returnData.result == "true") {
                    Ext.Msg.alert(extJsTitle,
                            "<fmt:message key='aimir.exist'/>", null, null);
                    meterRegMdsIdCheck = false;
                    $('#mdsId').val("");
                    $('#mdsId').focus();
                } else if(returnData.result=="delete"){
                    Ext.Msg.alert(extJsTitle,
                            "<fmt:message key='aimir.cannot.use'/>", null, null);
                    meterRegMdsIdCheck = false;
                    $('#mdsId').val("");
                    $('#mdsId').focus();
                } else {
                    Ext.Msg.alert(extJsTitle,
                            $('#mdsId').val()+" <fmt:message key='aimir.abailableId'/>", null, null);
                    meterRegMdsIdCheck = true;
                }
            });
        }
    }

    // 숫자 확인
    function getNumberValidCheck(input_number){
        if(isNaN(input_number)){
            Ext.Msg.alert(extJsTitle, " <fmt:message key='aimir.msg.onlydigit'/>", null, null);
        }else{
            isNumberCheck = true;
        }
    }
    // 설치현황 > 이미지 관련 JS --

    var installImgArray = null;

    // 이미지 저장
    var insertMeterInstallImg = function(orgFileName, saveFileName) {
        $.getJSON('${ctx}/gadget/device/insertMeterInstallImg.do', {
            'meterId' : meterId,
            'orgFileName' : orgFileName,
            'saveFileName' : saveFileName
        },

        function(data, status) {
            installImgArray = data.installImgList;
            setInstallImgBar(installImgArray.length);
        });
    };

    // 이미지 초기 조회
    function getInstallImg() {
        $.getJSON('${ctx}/gadget/device/getInsertInstallImg.do', {
            'meterId' : meterId
        }, function(data) {
            installImgArray = data.installImgList;
            setInstallImgBar(1);
        });
    }

    // 설치 이미지 Bar설정
    function setInstallImgBar(curPage) {
        var imgArray = installImgArray;

        if (imgArray != null && imgArray != "") {
            var meterInstallImgPaging = "<ul><li>";
            var curPageImg = "";
            var curPageId = "";

            $.each(imgArray, function(index, imgData) {
                var indexValue = index + 1;

                meterInstallImgPaging = meterInstallImgPaging
                        + "<a href=\"javascript:setInstallImgBar('"
                        + indexValue + "');\" ";

                // 선택한 page
                if (indexValue == curPage) {
                    meterInstallImgPaging = meterInstallImgPaging
                            + " class=\"current\" ";

                    curPageImg = imgData.saveFileName;
                    curPageId = imgData.id;
                }

                meterInstallImgPaging = meterInstallImgPaging + ">"
                        + indexValue + "</a>";
            });

            meterInstallImgPaging = meterInstallImgPaging + "</ul></li>";

            // 이미지 ID 설정
            $("#meterInstallImgId").val(curPageId);

            // 이미지 설정
            $("#meterInstallImgEdit").html(
                    "<img src=\"../../" + curPageImg + "\" >");
            $("#meterInstallImgView").html(
                    "<img src=\"../../" + curPageImg + "\" >");

            // 설치 이미지 Bar설정
            $("#meterInstallImgPagingEdit").html(meterInstallImgPaging);
            $("#meterInstallImgPagingView").html(meterInstallImgPaging);
        } else {
            // 이미지 ID 설정
            $("#meterInstallImgId").val("");

            // 이미지 설정
            $("#meterInstallImgEdit").html(
                    "<img src='../../uploadImg/default/meterDefaultImg.jpg'>");
            $("#meterInstallImgView").html(
                    "<img src='../../uploadImg/default/meterDefaultImg.jpg'>");

            // 설치 이미지 Bar설정
            $("#meterInstallImgPagingEdit").html("");
            $("#meterInstallImgPagingView").html("");
        }
    }

    // installInfoDiv의  hide/show
    var changeInstallDivDisplay = function(target) {
        $('#meterInstallInfoEditDiv').hide();
        $('#meterInstallInfoViewDiv').hide();

        if (target == "edit") {
            $('#meterInstallInfoEditDiv').show();

            // hidden의 값을 복사함 - Meter유형별로 변경필요
            $('#customerNo').val($('#customerNoHidden').val());
            $('#customerName').val($('#customerNameHidden').val());
            $('#modemSysId').val($('#modemSysIdHidden').val());
            $('#supplierId').val($('#supplierIdHidden').val());
            $('#supplierName').val($('#supplierNameHidden').val());
            $('#alarmStatus').val($('#alarmStatusHidden').val());
            $('#switchStatus').val($('#switchStatusHidden').val());
            $('#esolution').val($('#esolutionHidden').val());
            $('#ke').val($('#keHidden').val());

        } else if (target == "view") {
            $('#meterInstallInfoViewDiv').show();
        }
    };

    // 장비설치 > 설치 이미지 등록
    function getModelListByVendor(setState) {
        if ($('#deviceVendor').val() != "")
            $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do',
                      {
                          'vendorId' : $('#deviceVendor').val()
                      }, function(returnData) {
                            $('#modelId').noneSelect(
                                    returnData.deviceModels);
                            $('#modelId').selectbox();

                            $('#modelIdEdit').noneSelect(
                                    returnData.deviceModels);
                            $('#modelIdEdit').selectbox();

                            // getMeter등의 정보로 Model을 설정해야 하는 경우 set을 표시함
                            if (setState == 1) {
                                $('#modelId').option($('#modelIdHidden').val());
                                $('#modelIdEdit').option($('#modelIdHidden').val());

                                $('#modelIdView').val($('#modelId option:selected').text());
                            }
                      });
    };

    function getDeviceVendorsBySupplierId() {
        $.getJSON('${ctx}/gadget/system/vendorlist.do', {
            'supplierId' : supplierId
        }, function(returnData) {
            $('#sVendor').loadSelect(returnData.deviceVendors);
            $('#sVendor').selectbox();
        });
    };
    //미터 그룹 초기화.
    function getMeterGroupBygroupId() {
        $.getJSON('${ctx}/gadget/system/getMeterGroupBygroupId.do', {
            'supplierId' : supplierId,
            'groupType' : 'Meter'
        }, function(returnData) {
            $('#sMeterGroup').loadSelect(returnData.NAME);
            $('#sMeterGroup').selectbox();
        });
    };

    function getDeviceModelsByVenendorId() {
        if ($('#sVendor').val() != "")
            $.getJSON('${ctx}/gadget/system/getDeviceModelsByVenendorId.do', {
                'vendorId' : $('#sVendor').val()
            }, function(returnData) {
                $('#sModel').noneSelect(returnData.deviceModels);
                $('#sModel').selectbox();
            });
    };

    // 검침데이터 Tab 관련 JS --
    function send() {
        // CommonDateTab에서 send조회
        meteringByMeterSearch();
    }
    function meteringByMeterSearch() {
        fcMeasureChartUpdate();
        renderMeteringGrid();
    }

    function getUnitCondition(getunit){
        if(getunit == "EnergyMeter"){
            return "[kWh]";
        }else{
            return "[m3]";
        }
    }

    function getMeteringByMeterCondition() {

        var arrayObj = Array();
        var meterType = $('#meterType').val();
        var option = MeterTypeMap[meterType];

        arrayObj[0] = meterId;
        arrayObj[1] = option.displayName;

        arrayObj[2] = $('#searchStartDate').val();
        arrayObj[3] = $('#searchEndDate').val();
        arrayObj[4] = $('#searchStartHour').val();
        arrayObj[5] = $('#searchEndHour').val();
        arrayObj[6] = $('#searchDateType').val();
        arrayObj[7] = supplierId;

        return arrayObj;
    }

    function getMeteringByMeterGrid() {
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.meteringtime"/>";
        fmtMessage[2] = "<fmt:message key="aimir.usage"/>";
        fmtMessage[3] = "<fmt:message key="aimir.co2formula"/>";

        var dataFild = new Array();
        dataFild[0] = "No";
        dataFild[1] = "meteringDate";
        dataFild[2] = "usage";
        dataFild[3] = "co2";

        var gridAlign = new Array();
        gridAlign[0] = "center";
        gridAlign[1] = "center";
        gridAlign[2] = "right";
        gridAlign[3] = "right";

        var gridWidth = new Array();
        gridWidth[0] = "500 ";
        gridWidth[1] = "1000";
        gridWidth[2] = "1000";
        gridWidth[3] = "1000";

        var dataGrid = new Array();
        dataGrid[0] = fmtMessage;
        dataGrid[1] = dataFild;
        dataGrid[2] = gridAlign;
        dataGrid[3] = gridWidth;

        return dataGrid;
    }

    // 주소 정보 -> X,Y,Z로 변경함
    function getGeoCoding() {
        cvAddressToCoordinate($('#sysLocation').val(), "gpioX", "gpioY", "gpioZ");
    }

    // 지도의 위도  / 경도 변경
    function updateMeterLoc() {
        $.getJSON('${ctx}/gadget/device/mapUpdate.do',
                {
                    'className' : "meter",
                    'name' : meterMds,
                    'pointx' : $('#gpioX').val(),
                    'pointy' : $('#gpioY').val()
                },
                function(returnData) {
                    Ext.MessageBox.show({
                        title : '<fmt:message key='aimir.meter'/> <fmt:message key='aimir.info'/>',
                        buttons : Ext.MessageBox.OK,
                        msg : '<fmt:message key='aimir.alert.geographicUpdate'/>',
                        icon : Ext.MessageBox.INFO,
                        fn : function() {
                            viewMeterMap();
                        }
                    });
                });
    }

    // 주소 업데이트
    function updateMeterAddress() {
        $.getJSON('${ctx}/gadget/device/mapUpdateAddress.do',
                {
                    'className' : "meter",
                    'name' : meterMds,
                    'address' : encodeURIComponent($('#sysLocation').val())
                },
                function(returnData) {
                    Ext.MessageBox.show({
                        title : '<fmt:message key='aimir.meter'/> <fmt:message key='aimir.info'/>',
                        buttons : Ext.MessageBox.OK,
                        msg : '<fmt:message key='aimir.alert.geographicUpdate'/>',
                        icon : Ext.MessageBox.INFO
                    });
                });
    }

    function viewMeterMap() {
        // googleMap 초기화
        if(googleMapInit()) {
             // meter의 정보를 구함
            showPoints('${ctx}/' + supplierId + '/' + meterMds + '/meter.do');
        }
    }

    function init() {
        // 검색 > 공급사의 제조사 조회
        getDeviceVendorsBySupplierId();
        // 검색 > 미터그룹 리스트
        getMeterGroupBygroupId();

        // 상세조회 > 일반 초기화
        initMeterDetail();

        hide();
    }

    // 조건 유효성체크
    function onDemandValidate() {
        var isValid = false;

        if ($('#onDemandFromDateHidden').val().length < 8) { // 일자 값이 8자리 이하일 경우 에러
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.message.startdate.empty'/>");
        } else if ($('#onDemandToDateHidden').val().length < 8) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.message.enddate.empty'/>");
        } else if (Number($('#onDemandFromDateHidden').val()) > Number($(
                '#onDemandToDateHidden').val())) { // 시작일자가 더 크면 에러
            // 시작일자가 더 크면 에러
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.season.error'/>");
        } else if (calDateRange($('#onDemandFromDateHidden').val(), $(
                '#onDemandToDateHidden').val()) > 6) {
            // 기간 차이가 7일 이상 이면 에러
            Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.device.onlySearch7days'/>");
        } else {
            isValid = true;
        }

        return isValid;
    }

    // 날짜사이 일자 계산
    function calDateRange(startDate, endDate) {
        var startDateArr = new Array();
        startDateArr.push(startDate.substring(0, 4));
        startDateArr.push((Number(startDate.substring(4, 6)) - 1) + "");
        startDateArr.push(startDate.substring(6, 8));

        var endDateArr = new Array();
        endDateArr.push(endDate.substring(0, 4));
        endDateArr.push((Number(endDate.substring(4, 6)) - 1) + "");
        endDateArr.push(endDate.substring(6, 8));

        var startDateDt = new Date(startDateArr[0], startDateArr[1], startDateArr[2]);
        var endDateDt = new Date(endDateArr[0], endDateArr[1], endDateArr[2]);

        return (endDateDt.getTime() - startDateDt.getTime()) / 1000 / 60 / 60 / 24;
    }

    function onDemand() {
        if (!onDemandValidate()) {
            return;
        }
        //비동기 설정
        $.ajaxSetup({
            async : true
        });
        Ext.Msg.wait('Waiting for response.', 'Wait !');

        $
                .getJSON(
                        '${ctx}/gadget/device/command/cmdOnDemand.do',
                        {
                            'target' : meterId,
                            'loginId' : loginId,
                            'fromDate' : $('#onDemandFromDateHidden').val()
                                    + "000000", //onDemandFromDateHidden
                            'toDate' : $('#onDemandToDateHidden').val()
                                    + "000000"
                        },
                        function(returnData) {
                            //원래 동기방식으로 설정
                            $.ajaxSetup({
                                async : false
                            });
                            $('#commandResult').val(returnData.rtnStr);
                            Ext.Msg.hide();
                            if (returnData.rtnStr == 'Success') {
                                Ext.Msg.alert('', 'Success!', null, null);
                                document.getElementById("detail_view").innerHTML = returnData.detail;
                                if (returnData.detail != null
                                        && returnData.detail != "<html></html>") {
                                    if ($("#isMx2").val() == "true") {
                                        $("#phasorDiagramTbl").show();
                                        var svgLink = "<img src='${ctx}/gadget/device/viewPhasorDiagram.do"
                                                + "?volAng_a="
                                                + $("#volAng_a").val()
                                                + "&volAng_b="
                                                + $("#volAng_b").val()
                                                + "&volAng_c="
                                                + $("#volAng_c").val()
                                                + "&curAng_a="
                                                + $("#curAng_a").val()
                                                + "&curAng_b="
                                                + $("#curAng_b").val()
                                                + "&curAng_c="
                                                + $("#curAng_c").val() + "'>";
                                        $("#phasorDiagram").html(svgLink);

                                        var arrays = new Array();
                                        arrays[0] = [ $("#volAng_a").val(),
                                                $("#volAng_b").val(),
                                                $("#volAng_c").val(),
                                                $("#curAng_a").val(),
                                                $("#curAng_b").val(),
                                                $("#curAng_c").val() ];
                                        viewPhaseAngle(arrays);
                                    }

                                    $('#detail_dialog').dialog('open');
                                }
                            } else {
                                Ext.Msg.alert('', returnData.rtnStr, null, null);
                            }
                        });
    }

    function cmdRelayStatus() {

        var mcuId = $('#mcuSysId').val();

        //비동기 설정
        $.ajaxSetup({
            async : true
        });

        $('#commandResult').val("");
        Ext.Msg.wait('Waiting for response.', 'Wait !');

        $.getJSON('${ctx}/gadget/device/command/cmdRemoteGetStatus.do', {
            'target' : meterId,
            'mcuId' : mcuId,
            'loginId' : loginId
        }, function(returnData) {
            //원래 동기방식으로 설정
            $.ajaxSetup({
                async : false
            });
            Ext.Msg.hide();
            var rtnStr = returnData.rtnStr;

            $('#commandResult').val(rtnStr);

            if (rtnStr == 'Success') {
                Ext.Msg.alert('', 'Success!', null, null);
            } else {
                Ext.Msg.alert('', 'Done', null, null);
            }

        });
    }

    function cmdRelayOff() {
        var mcuId = $('#mcuSysId').val();

        //비동기 설정
        $.ajaxSetup({
            async : true
        });

        $('#commandResult').val("");
        Ext.Msg.wait('Waiting for response.', 'Wait !');
        $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerOff.do', {
            'target' : meterId,
            'mcuId' : mcuId,
            'loginId' : loginId
        }, function(returnData) {
            //원래 동기방식으로 설정
            $.ajaxSetup({
                async : false
            });
            Ext.Msg.hide();
            $('#commandResult').val(returnData.rtnStr);
            if (returnData.rtnStr == 'Success') {
                Ext.Msg.alert('', 'Success!', null, null);
            } else {
                Ext.Msg.alert('', 'Done', null, null);
            }
            $('#switchStatus').val(returnData.relayStatus);
        });
    }

    function cmdRelayOn() {

        var mcuId = $('#mcuSysId').val();
        //비동기 설정
        $.ajaxSetup({
            async : true
        });

        $('#commandResult').val("");

        Ext.Msg.wait('Waiting for response.', 'Wait !');

        $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerOn.do', {
            'target' : meterId,
            'mcuId' : mcuId,
            'loginId' : loginId
        }, function(returnData) {
            //원래 동기방식으로 설정
            $.ajaxSetup({
                async : false
            });
            Ext.Msg.hide();
            $('#commandResult').val(returnData.rtnStr);
            if (returnData.rtnStr == 'Success') {
                Ext.Msg.alert('', 'Success!', null, null);
            } else {
                Ext.Msg.alert('', 'Done', null, null);
            }
            $('#switchStatus').val(returnData.relayStatus);
        });
    }

    function cmdRelayActivate() {
        var mcuId = $('#mcuSysId').val();

        //비동기 설정
        $.ajaxSetup({
            async : true
        });

        $('#commandResult').val("");
        Ext.Msg.wait('Waiting for response.', 'Wait !');
        $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerActivate.do', {
            'target' : meterId,
            'mcuId' : mcuId,
            'loginId' : loginId
        }, function(returnData) {
            //원래 동기방식으로 설정
            $.ajaxSetup({
                async : false
            });
            Ext.Msg.hide();
            $('#commandResult').val(returnData.rtnStr);
            if (returnData.rtnStr == 'Success') {
                Ext.Msg.alert('', 'Success!', null, null);
            } else {
                Ext.Msg.alert('', 'Done', null, null);
            }
        });
    }

    function cmdMeterTimeSync() {
        var mcuId = $('#mcuSysId').val();
        //비동기 설정
        $.ajaxSetup({
            async : true
        });

        $('#commandResult').val("");
        Ext.Msg.wait('Waiting for response.', 'Wait !');
        $.getJSON('${ctx}/gadget/device/command/cmdMeterTimeSync.do', {
            'target' : meterId,
            'mcuId' : mcuId,
            'loginId' : loginId
        }, function(returnData) {
            //원래 동기방식으로 설정
            $.ajaxSetup({
                async : false
            });

            Ext.Msg.hide();
            $('#commandResult').val(returnData.rtnStr);
            if (returnData.rtnStr == 'Success') {
                Ext.Msg.alert('', 'Success!', null, null);
            } else {
                Ext.Msg.alert('', 'Done', null, null);
            }
        });
    }
    
    // 미터에는 IP가 없기때문에 물려있는 MODEM IP로 ping/traceroute 명령을 한다.
    function commandPing(){
    	Ext.Msg.prompt('Options For Ping', 'Please enter packet-size and echo-count.<br/><br/>Desc) packet-size, echo-count <br/>',
    			function(btn, text) {
    				if(btn=='ok'){
    					var data = text;
    					var packetSize = 0;
    					var count = 0;
    					if(data != null) {
    						var trimedData = data.replaceAll(" ", "");
							var splitedData = trimedData.split(',');
				
							if(2 < splitedData.length || splitedData.length < 1) {
								Ext.Msg.alert('Error', 'Please re-enter packet-size and echo-count.');
								return false;
							}
							
							// 입력된 값이 숫자가 아닐 경우 오류 처리
							if(isNaN(splitedData[0]) == true || isNaN(splitedData[1]) == true) {
								Ext.Msg.alert('Error', 'Please enter number.');
								return false;
							} else {
								doPing(splitedData[0], splitedData[1]);
							}
						} else {
    						Ext.Msg.alert('Error', 'Please re-enter packet-size and echo-count.');
							return false;
    					}
    				}
    	},this, false, '64, 3');
    }

    function doPing(packetSize, count) {
    	var modemId = $('#modemId').val();
    	$('#commandResult').val("Request Modem Server Ping....");
        $.getJSON('${ctx}/gadget/device/command/cmdModemPing.do'
                , {'target' : modemId
                , 'loginId' : loginId
                , 'packetSize' : packetSize
                , 'count' : count
                , 'device' : 'meter'
                }
                , function (returnData){
                    if(!returnData.status){
                        $('#commandResult').val("FAIL");
                           return;
                    }
                    if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                        $('#commandResult').val("Modem Ping: "+returnData.status);
                    }else{
                    	$('#commandResult').val(""); 
                        $('#commandResult').val(returnData.jsonString); 
                    }
                });	
    }
	
    function commandTraceroute() {
    	var modemId = $('#modemId').val();
    	$('#commandResult').val("Request MODEM Traceroute....");
        $.getJSON('${ctx}/gadget/device/command/cmdModemTraceroute.do'
        		, {'target' : modemId
                , 'loginId' : loginId}
                , function (returnData){
                    if(!returnData.status){
                        $('#commandResult').val("FAIL");
                           return;
                    }
                    if(returnData.status.length>0 && returnData.status!='SUCCESS'){
                        $('#commandResult').val("MCU Traceroute: "+returnData.status);
                    }else{
                    	$('#commandResult').val(""); 
                        $('#commandResult').val(returnData.jsonString); 
                    }
                });
    }
    
    function cmdTOUCalendar() {
        var meterSerialNumber = $('#mdsIdHidden').val();
        //비동기 설정
        $.ajaxSetup({
            async : true
        });

        Ext.Msg.wait('Waiting for response.', 'Wait !');
        $.getJSON('${ctx}/gadget/device/command/cmdTOUCalendar.do', {
            'meterSerial' : meterSerialNumber,
            'loginId' : loginId
        }, function(returnData) {
            //원래 동기방식으로 설정
            $.ajaxSetup({
                async : false
            });

            Ext.Msg.hide();
            $('#commandResult').val(returnData.rtnStr);
            if (returnData.rtnStr == 'Success') {
                Ext.Msg.alert('', 'Success!', null, null);
            } else {
                Ext.Msg.alert('', 'Done', null, null);
            }
        });
    }

    function cmdDisplayItemSetting() {
        var meterSerialNumber = $('#mdsIdHidden').val();
        //비동기 설정
        $.ajaxSetup({
            async : true
        });

        Ext.Msg.wait('Waiting for response.', 'Wait !');
        $.getJSON('${ctx}/gadget/device/command/cmdDisplayItemSetting.do', {
            'meterSerial' : meterSerialNumber,
            'loginId' : loginId
        }, function(returnData) {
            //원래 동기방식으로 설정
            $.ajaxSetup({
                async : false
            });

            Ext.Msg.hide();
            $('#commandResult').val(returnData.rtnStr);
            if (returnData.rtnStr == 'Success') {
                Ext.Msg.alert('', 'Success!', null, null);
            } else {
                Ext.Msg.alert('', 'Done', null, null);
            }
        });
    }

    // Demand Reset
    function cmdDemandReset() {
        $('#commandResult').val("");
        $.getJSON('${ctx}/gadget/device/command/cmdDemandReset.do', {
            'target' : meterId,
            'loginId' : loginId
        }, function(returnData) {
            $('#commandResult').val(returnData.rtnStr);
        });
    }

    function cmdGetMeterEvent() {
        //비동기 설정
        $.ajaxSetup({
            async : true
        });
        Ext.Msg.wait('Waiting for response.', 'Wait !');

        $
                .getJSON(
                        '${ctx}/gadget/device/command/cmdOnDemandWithOption.do',
                        {
                            'target' : meterId,
                            'loginId' : loginId,
                            'fromDate' : '',
                            'toDate' : '',
                            'nOption' : '10',
                        },
                        function(returnData) {
                            //원래 동기방식으로 설정
                            $.ajaxSetup({
                                async : false
                            });
                            $('#commandResult').val(returnData.rtnStr);
                            Ext.Msg.hide();
                            if (returnData.rtnStr == 'Success') {
                                Ext.Msg.alert('', 'Success!', null, null);
                                document.getElementById("detail_view").innerHTML = returnData.detail;
                                if (returnData.detail != null
                                        && returnData.detail != "<html></html>") {
                                    $('#detail_dialog').dialog('open');
                                }
                            } else {
                                Ext.Msg.alert('', returnData.rtnStr, null, null);
                            }
                        });
    }

    function cmdGetBilling() {
        //비동기 설정
        $.ajaxSetup({
            async : true
        });
        Ext.Msg.wait('Waiting for response.', 'Wait !');

        $
                .getJSON(
                        '${ctx}/gadget/device/command/cmdOnDemandWithOption.do',
                        {
                            'target' : meterId,
                            'loginId' : loginId,
                            'fromDate' : '',
                            'toDate' : '',
                            'nOption' : '2',
                        },
                        function(returnData) {
                            //원래 동기방식으로 설정
                            $.ajaxSetup({
                                async : false
                            });
                            $('#commandResult').val(returnData.rtnStr);
                            Ext.Msg.hide();
                            if (returnData.rtnStr == 'Success') {
                                Ext.Msg.alert('', 'Success!', null, null);
                                document.getElementById("detail_view").innerHTML = returnData.detail;
                                if (returnData.detail != null
                                        && returnData.detail != "<html></html>") {
                                    $('#detail_dialog').dialog('open');
                                }

                            } else {
                                Ext.Msg.alert('', returnData.rtnStr, null, null);
                            }
                        });
    }

    //SX2 롤백 기능 - nOption : 12
    function cmdRestoreDefaultFW() {
        //비동기 설정
        $.ajaxSetup({
            async : true
        });
        Ext.Msg.wait('Waiting for response.', 'Wait !');

        $
                .getJSON(
                        '${ctx}/gadget/device/command/cmdOnDemandWithOption.do',
                        {
                            'target' : meterId,
                            'loginId' : loginId,
                            'fromDate' : '',
                            'toDate' : '',
                            'nOption' : '12',
                        },
                        function(returnData) {
                            //원래 동기방식으로 설정
                            $.ajaxSetup({
                                async : false
                            });
                            $('#commandResult').val(returnData.rtnStr);
                            Ext.Msg.hide();
                            if (returnData.rtnStr == 'Success') {
                                Ext.Msg.alert('', 'Success!', null, null);
                                document.getElementById("detail_view").innerHTML = returnData.detail;
                                if (returnData.detail != null
                                        && returnData.detail != "<html></html>") {
                                    $('#detail_dialog').dialog('open');
                                }

                            } else {
                                Ext.Msg.alert('', returnData.rtnStr, null, null);
                            }
                        });
    }

    $(function() {
        if (permitLocationId != null && permitLocationId != "") {
            locationTreeForPermitLocation('treeDivA', 'searchWord_1', 'sLocationId', permitLocationId);
        } else {
            locationTreeGoGo('treeDivA', 'searchWord_1', 'sLocationId');
        }
    });

    /************ Fusion Chart **************/
    function showLogChart() {
        var colWidth = 0;
        if ($('#fcLogChartDiv').width() > 0) {
            colWidth = $('#fcLogChartDiv').width();
        } else if ($('#fcMeasureChartDiv').width() > 0) {
            colWidth = $('#fcMeasureChartDiv').width();
        } else if ($('#general').width() > 0) {
            colWidth = $('#general').width() - 42;
        } else if ($('#locationInfo').width() > 0) {
            colWidth = $('#locationInfo').width() - 42;
        }

        fcLogChart = new FusionCharts(
                "${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "fcLogChartId",
                colWidth, "250", "0", "0");
        fcLogChart.setDataXML(fcLogChartDataXml);
        fcLogChart.setTransparent("transparent");
        fcLogChart.render("fcLogChartDiv");
    }

    function showMeasureChart() {
        var colWidth = 0;
        if ($('#fcLogChartDiv').width() > 0) {
            colWidth = $('#fcLogChartDiv').width();
        } else if ($('#fcMeasureChartDiv').width() > 0) {
            colWidth = $('#fcMeasureChartDiv').width();
        } else if ($('#general').width() > 0) {
            colWidth = $('#general').width() - 42;
        } else if ($('#locationInfo').width() > 0) {
            colWidth = $('#locationInfo').width() - 42;
        }

        fcMeasureChart = new FusionCharts(
                "${ctx}/flexapp/swf/fcChart/MSColumn3D.swf",
                "fcMeasureChartId", colWidth, "250", "0", "0");
        fcMeasureChart.setDataXML(fcMeasureChartDataXml);
        fcMeasureChart.setTransparent("transparent");
        fcMeasureChart.render("fcMeasureChartDiv");
        fcMeasureChart.addEventListener("DrawComplete", function() {
            meteringByMeterSearch();
        });
    }

    function updateFChart() {
        emergePre();

        if ($('#sInstallStartDate').val() == '')
            $('#sInstallStartDateHidden').val('');
        if ($('#sInstallEndDate').val() == '')
            $('#sInstallEndDateHidden').val('');
        if ($('#sLastcommStartDate').val() == '')
            $('#sLastcommStartDateHidden').val('');
        if ($('#sLastcommEndDate').val() == '')
            $('#sLastcommEndDateHidden').val('');

        var condition = getCondition();
        $.getJSON('${ctx}/gadget/device/getMeterSearchChart.do',
                {sMeterType : condition[0],
                 sMdsId : condition[1],
                 sMeterGroup : condition[17],
                 sStatus : condition[2],
                 sMcuName : condition[3],
                 sLocationId : condition[4],
                 sConsumLocationId : condition[5],
                 sVendor : condition[6],
                 sModel : condition[7],
                 sInstallStartDate : condition[8],
                 sInstallEndDate : condition[9],
                 sModemYN : condition[10],
                 sCustomerYN : condition[11],
                 sLastcommStartDate : condition[12],
                 sLastcommEndDate : condition[13],
                 supplierId : condition[16],
                 sCommState : condition[15],
                 sCustomerId : condition[18],
                 sCustomerName : condition[19],
                 sPermitLocationId : condition[20],
                 sMeterAddress : condition[21]
                },
                function(json) {
                    var list = json.chartData;
                    fcPieChartDataXml = "<chart "
                                      + "showPercentValues='1' "
                                      + "showPercentInToolTip='0' "
                                      + "showLabels='0' "
                                      + "showValues='1' "
                                      + "showLegend='1' "
                                      + "legendPosition='Bottom' "
                                      + "manageLabelOverflow='1' "
                                      + "useEllipsesWhenOverflow='0' "
                                      + fChartStyle_Common
                                      + fChartStyle_Font
                                      + fChartStyle_Pie3D
                                      + ">";
                    var labels = "";

                    var emptyFlag = true;
                    for (index in list) {
                        if (list[index].label == "fmtMessage") {
                            labels += "<set label='<fmt:message key='aimir.commstateGreen' />' value='"
                                    + list[index].data
                                    + "' color='"
                                    + fChartColor_Step5[0] + "' />";
                            if (list[index].data > 0)
                                emptyFlag = false;
                        } else if (list[index].label == "fmtMessage24") {
                            labels += "<set label='<fmt:message key='aimir.commstateYellow' />' value='"
                                    + list[index].data
                                    + "' color='"
                                    + fChartColor_Step5[2] + "' />";
                            if (list[index].data > 0)
                                emptyFlag = false;
                        } else if (list[index].label == "fmtMessage48") {
                            labels += "<set label='<fmt:message key='aimir.commstateRed' />' value='"
                                    + list[index].data
                                    + "' color='"
                                    + fChartColor_Step5[3] + "' />";
                            if (list[index].data > 0)
                                emptyFlag = false;
                        } else if (list[index].label == "fmtMessage99") {
                            labels += "<set label='<fmt:message key='aimir.bems.facilityMgmt.unknown' />' value='"
                                    + list[index].data
                                    + "' color='"
                                    + fChartColor_Step5[4] + "' />";
                            if (list[index].data > 0)
                                emptyFlag = false;
                        }
                    }

                    if (list == null || list.length == 0 || emptyFlag) {
                        labels = "<set label='' value='1' color='E9E9E9' toolText='' />";
                    }

                    fcPieChartDataXml += labels + "</chart>";
                    fcChartRender();

                    hide();
                });
    }

    function initLogChartData() {
        fcLogChartDataXml = "<chart "
               + "showPercentValues ='1'"
               + "showValues='0' "
               + "showLabels='0' "
               + "showLegend='1' "
               + "legendPosition='RIGHT' "
               + fChartStyle_Common
               + fChartStyle_Font
               + fChartStyle_MSColumn3D_nobg
               + ">"
                + "<categories><category label=' ' /></categories>"
                + "<dataset seriesName='<fmt:message key='aimir.commlog'/>'><set value='0' /></dataset>"
                + "<dataset seriesName='<fmt:message key='aimir.changehistory'/>'><set value='0' /></dataset>"
                + "<dataset seriesName='<fmt:message key='aimir.alerthistory'/>'><set value='0' /></dataset>"
                + "<dataset seriesName='<fmt:message key='aimir.view.operationlog'/>'><set value='0' /></dataset>"
                + "</chart>";
    }

    function fcLogChartUpdate() {
        emergePre();

        $.getJSON('${ctx}/gadget/device/getMeterLogChart.do',
                  {meterMds : meterMds,
                   startDate : $('#btn_searchStartDate').val(),
                   endDate : $('#btn_searchEndDate').val(),
                   supplierId : supplierId
                  },
                  function(json) {
                      var list = json.chartData;
                      fcLogChartDataXml = "<chart " + "showValues='0' "
                              + "showLabels='0' " + "showLegend='1' "
                              + "showPercentValues='1'"
                              + "legendPosition='RIGHT' "
                              + "labelDisplay = 'WRAP' " + "labelStep='"
                              + (list.length / 8) + "' "
                              + fChartStyle_Common + fChartStyle_Font
                              + fChartStyle_MSColumn3D_nobg + ">";
                      var categories = "<categories>";
                      var dataset1 = "<dataset seriesName='<fmt:message key='aimir.commlog'/>'>";
                      var dataset2 = "<dataset seriesName='<fmt:message key='aimir.changehistory'/>'>";
                      var dataset3 = "<dataset seriesName='<fmt:message key='aimir.alerthistory'/>'>";
                      var dataset4 = "<dataset seriesName='<fmt:message key='aimir.view.operationlog'/>'>";

                      for (index in list) {
                          if (list[index].xTag != null) {
                              categories += "<category label='"+list[index].xTag +"' />";

                              dataset1 += "<set value='"
                                      + list[index].commLog
                                      + "' link=\"JavaScript:viewLogGrid( 'commLog' );\" />";
                              dataset2 += "<set value='"
                                      + list[index].updateLog
                                      + "' link=\"JavaScript:viewLogGrid( 'updateLog' );\" />";
                              dataset3 += "<set value='"
                                      + list[index].brokenLog
                                      + "' link=\"JavaScript:viewLogGrid( 'brokenLog' );\" />";
                              dataset4 += "<set value='"
                                      + list[index].operationLog
                                      + "' link=\"JavaScript:viewLogGrid( 'operationLog' );\" />";
                          }
                      }

                      if (list == null || list.length == 0) {
                          categories += "<category label=' ' />";

                          dataset1 += "<set value='0' />";
                          dataset2 += "<set value='0' />";
                          dataset3 += "<set value='0' />";
                          dataset4 += "<set value='0' />";
                      }

                      categories += "</categories>";
                      dataset1 += "</dataset>";
                      dataset2 += "</dataset>";
                      dataset3 += "</dataset>";
                      dataset4 += "</dataset>";

                      fcLogChartDataXml += categories + dataset1
                              + dataset2 + dataset3 + dataset4
                              + "</chart>";
                      fcChartRender();

                      hide();
                  });
    }

    function initMeasureChartData() {
        fcMeasureChartDataXml = "<chart "
                + "showValues='0' "
                + "showLabels='1' "
                + "showLegend='1' "
                + "showPercentValues ='1'"
                + "legendPosition='RIGHT' "
                + "PYAxisName='<fmt:message key="aimir.usage"/>' "
                + fChartStyle_Common
                + fChartStyle_Font
                + fChartStyle_MSColumn3D_nobg
                + ">"
                + "<categories><category label=' ' /></categories>"
                + "<dataset seriesName='<fmt:message key='aimir.usage'/>[kWh]'><set value='0' /></dataset>"
                + "<dataset seriesName='<fmt:message key='aimir.co2formula2'/>[kg]' color='"
                + fChartColor_Step4[1]
                + "' >  <set value='0' color='"+fChartColor_Step4[1]+"' /></dataset>"
                + "</chart>";
    }

    function fcMeasureChartUpdate() {
        emergePre();

        var meterType = $('#meterType').val();

        var unit = "[kWh]";
        var option = MeterTypeMap[meterType];
        var unit = getUnitCondition(option.displayName);

        $.getJSON('${ctx}/gadget/device/getMeteringDataByMeterChart.do',
                  {meterId : meterId,
                   meterType : option.displayName,
                   searchStartDate : $('#searchStartDate').val(),
                   searchEndDate : $('#searchEndDate').val(),
                   searchStartHour : $('#searchStartHour').val(),
                   searchEndHour : $('#searchEndHour').val(),
                   searchDateType : $('#searchDateType').val(),
                   supplierId : supplierId
                  },
                  function(json) {
                      var list = json.chartData;

                      fcMeasureChartDataXml = "<chart "
                              + "showValues='0' "
                              + "showLabels='1' "
                              + "showLegend='1' "
                              + "PYAxisName='<fmt:message key="aimir.usage"/>' "
                              + "legendPosition='RIGHT' "
                              + "labelDisplay = 'WRAP' " + "labelStep='"
                              + (list.length / 8) + "' "
                              + fChartStyle_Common + fChartStyle_Font
                              + fChartStyle_MSColumn3D_nobg + ">";
                      var categories = "<categories>";
                      var dataset1 = "<dataset seriesName='<fmt:message key='aimir.usage'/>"+unit+"'>";
                      var dataset2 = "<dataset seriesName='<fmt:message key='aimir.co2formula2' />[kg]' color='"
                              + fChartColor_Step4[1] + "' >";

                      for (index in list) {
                          if (list[index].meteringDate != null) {
                              categories += "<category label='"+list[index].meteringDate +"' />";

                              dataset1 += "<set value='"+list[index].usage+"' />";
                              dataset2 += "<set value='"+list[index].co2+"' color='"+fChartColor_Step4[1]+"' />";
                          }
                      }

                      if (list == null || list.length == 0) {
                          categories += "<category label=' ' />";

                          dataset1 += "<set value='0' />";
                          dataset2 += "<set value='0' color='"+fChartColor_Step4[1]+"' />";
                      }

                      categories += "</categories>";
                      dataset1 += "</dataset>";
                      dataset2 += "</dataset>";

                      fcMeasureChartDataXml += categories + dataset1
                              + dataset2 + "</chart>";
                      fcChartRender();

                      hide();
                  });
    }

    $(window).resize(function() {
        fcChartRender();
        renderChartGrid();
        renderGrid();
    });

    function fcChartRender() {
        fcPieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf",
                "myChartId", $('#fcPieChartDiv').width(), "152", "0", "0");
        fcPieChart.setDataXML(fcPieChartDataXml);
        fcPieChart.setTransparent("transparent");
        fcPieChart.render("fcPieChartDiv");

        var colWidth = 0;
        if ($('#fcLogChartDiv').width() > 0) {
            colWidth = $('#fcLogChartDiv').width();
        } else if ($('#fcMeasureChartDiv').width() > 0) {
            colWidth = $('#fcMeasureChartDiv').width();
        } else if ($('#general').width() > 0) {
            colWidth = $('#general').width() - 42;
        } else if ($('#locationInfo').width() > 0) {
            colWidth = $('#locationInfo').width() - 42;
        }

        fcMeasureChart = new FusionCharts(
                "${ctx}/flexapp/swf/fcChart/MSColumn3D.swf",
                "fcMeasureChartId", colWidth, "250", "0", "0");
        fcMeasureChart.setDataXML(fcMeasureChartDataXml);
        fcMeasureChart.setTransparent("transparent");
        fcMeasureChart.render("fcMeasureChartDiv");
    }

    function clearAndHideOnDemandButton() {
        $('#onDemandFromDate').val('');
        $('#onDemandToDate').val('');
        $('#onDemandFromDateHidden').val('');
        $('#onDemandToDateHidden').val('');
        $('#selectDate').hide();
    }

    // 최종통신날짜 및 조건 검색에 대한 reset 설정
    function reset() {
        // Form Reset
        var $searchForm = $("form[name=search]");
        $searchForm.trigger("reset");
        
        // Form Reset으로 초기화되지 않는 인자 초기화
        $('#sLocationId').val("");

        // 셀렉트 태그 첫번째 인덱스 선택
        var $selects = $searchForm.find("select");
        $selects.each(function() {
            $(this).selectbox();
        });
        return;
    }

    // onDemand 결과창에서 Angle값 grid 생성
    function viewPhaseAngle(arrays) {
        var store = new Ext.data.ArrayStore({
            fields : [ 'volAng_a', 'volAng_b', 'volAng_c', 'curAng_a',
                    'curAng_b', 'curAng_c' ]
        });

        store.loadData(arrays);

        var colModel = new Ext.grid.ColumnModel({
            defaults : {
                width : 60,
                sortable : false
            },
            columns : [ {
                header : "I<small>A</small>",
                dataIndex : "curAng_a"
            }, {
                header : "I<small>B</small>",
                dataIndex : "curAng_b"
            }, {
                header : "I<small>C</small>",
                dataIndex : "curAng_c"
            }, {
                header : "V<small>A</small>",
                dataIndex : "volAng_a"
            }, {
                header : "V<small>B</small>",
                dataIndex : "volAng_b"
            }, {
                header : "V<small>C</small>",
                dataIndex : "volAng_c"
            } ]
        });

        if (arrays != null && arrays[0] != null) {
            var array = arrays[0];
            var len = array.length;

            for ( var i = 0; i < len; i++) {
                if (array[i] == '') {
                    colModel.setHidden(i, true);
                }
            }
        }

        //그리드 설정
        grid = new Ext.grid.GridPanel({
            height : 49,
            store : store,
            colModel : colModel,
            width : 340,
            renderTo : 'angleGrid',
            viewConfig : {
                forceFit : true
            }
        });
    }

    //report window(Excel)
    var win;
    function openExcelReport() {
        var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.number"/>";
        fmtMessage[1] = "<fmt:message key="aimir.meterid"/>";
        fmtMessage[2] = "<fmt:message key="aimir.metertype"/>";
        fmtMessage[3] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[4] = "<fmt:message key="aimir.vendor"/>";

        fmtMessage[5] = "<fmt:message key="aimir.model"/>";
        fmtMessage[6] = "<fmt:message key="aimir.modem.model"/>";
        fmtMessage[7] = "<fmt:message key="aimir.buildingMgmt.contractNumber"/>";
        fmtMessage[8] = "<fmt:message key="aimir.lastcomm"/>";
        fmtMessage[9] = "<fmt:message key="aimir.location"/>";

        fmtMessage[10] = "<fmt:message key="aimir.status"/>";

        fmtMessage[11] = "<fmt:message key="aimir.commstateGreen"/>";
        fmtMessage[12] = "<fmt:message key="aimir.commstateYellow"/>";
        fmtMessage[13] = "<fmt:message key="aimir.commstateRed"/>";

        fmtMessage[14] = "<fmt:message key="aimir.excel.meterList"/>";

        fmtMessage[15] = "<fmt:message key="aimir.customerid"/>";
        fmtMessage[16] = "<fmt:message key="aimir.customername"/>";
        fmtMessage[17] = "<fmt:message key="aimir.install"/>"+"<fmt:message key="aimir.id"/>";
        fmtMessage[18] = "<fmt:message key="aimir.energymeter.ct"/>";
        fmtMessage[19] = "<fmt:message key="aimir.installProperty"/>";
        fmtMessage[20] = "<fmt:message key="aimir.address"/>";
        fmtMessage[21] = "<fmt:message key="aimir.meter.transformerRatio"/>";

        fmtMessage[22] = "<fmt:message key="aimir.address1"/>";
        fmtMessage[23] = "<fmt:message key="aimir.address2"/>";
        fmtMessage[24] = "<fmt:message key="aimir.address3"/>";
        fmtMessage[25] = "<fmt:message key="aimir.meter.address"/>";
        fmtMessage[26] = "<fmt:message key="aimir.sw.hw.ver"/>";

        obj.condition = getCondition();
        obj.fmtMessage = fmtMessage;

        if(win)
            win.close();

        win = window.open("${ctx}/gadget/device/meterMaxExcelDownloadPopup.do",
                        "meterExcel", opts);
        win.opener.obj = obj;
    }

    function openExcelReport2() {
        var com = document.getElementById("ondemandTable");
        f.excelData.value = com.outerHTML;
        f.action = "excelView.jsp";
        f.target = "_blank";
        f.submit();
    }

    /**
     * 일별 화살표처리
     */
    function onDemandDateArrow(fieldname, bfDate, val) {
        $.getJSON("${ctx}/common/getDate.do",
            {searchDate : bfDate, addVal : val, supplierId : supplierId},
            function(json) {
                var dateId = '#' + fieldname;
                var dateHiddenId = '#' + fieldname + 'Hidden';

                $(dateId).val(json.searchDate);
                $(dateHiddenId).val(json.searchDate);

                $.getJSON("${ctx}/common/convertSearchDate.do",
                    {searchStartDate : $(dateHiddenId).val(), searchEndDate : $(dateHiddenId).val(), supplierId : supplierId},
                    function(json) {
                        $(dateHiddenId).val(json.searchStartDate);
                });
        });
    }

    /**
     * 현재 일자 조회
     */
    function initDateCondition() {
        onDemandDateArrow("onDemandFromDate", "", 0);
        onDemandDateArrow("onDemandToDate", "", 0);
    }

    // OnDemand
    function showOnDemand() {
        $("#selectDate").show();
        initDateCondition();
    }

</script>
</head>

<body onLoad="init();">
    <form name="search">

        <!-- Search Background (S) -->
        <!--  가젯의 가장 큰 layout  table은 search할수 있는 option들을 선택하는 것들로 구성.-->
        <div class="search-bg-withouttabs">
            <div class="searchoption-container">
                <table class="searchoption wfree" border=0>
                    <tr>
                        <td class="withinput" style="width: 80px"><fmt:message
                                key="aimir.metertype" /></td>
                        <td class="padding-r20px"><select id="sMeterType" style="width: 190px;" name="select">
                                <option value=""><fmt:message key="aimir.all" /></option>
                                <c:forEach var="meterType" items="${meterType}">
                                    <c:choose>
                                        <c:when test="${not empty meterType.descr}">
                                            <option value="${meterType.name}">${meterType.descr}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${meterType.name}">${meterType.descr}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                        </select></td>

                        <td class="withinput" style="width: 80px"><fmt:message
                                key="aimir.meterid" /></td>
                        <td class="padding-r20px">
                            <input type="text" id="sMdsId" style="width: 190px;" />
                        </td>

                        <!-- Group -->
                        <td class="withinput" style="width: 100px"><fmt:message
                                key="aimir.metergroup" /></td>
                        <td class="padding-r20px"><select id="sMeterGroup" name="select" style="width: 120px;">
                                <option value=""><fmt:message key="aimir.all" /></option>
                        </select></td>

                        <td class="withinput"><fmt:message key="aimir.status" /></td>
                        <%-- <td colspan="1"><select id="sStatus" name="select" style="width: 190px;">
                                <option value=""><fmt:message key="aimir.all" /></option>
                                <c:forEach var="meterStatus" items="${meterStatus}">
                                    <option value="${meterStatus.id}">${meterStatus.descr}</option>
                                </c:forEach>
                        </select></td>
                        <td></td> --%>
                        <td class="padding-r20px"><select id="sStatus" name="select" style="width: 190px;">
                                <option value=""><fmt:message key="aimir.all" /></option>
                                <c:forEach var="meterStatus" items="${meterStatus}">
                                    <option value="${meterStatus.id}">${meterStatus.descr}</option>
                                </c:forEach>
                        </select></td>

                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.location" /></td>
                        <td class="padding-r20px"><input name="searchWord_1"
                            id='searchWord_1' type="text" style="width: 190px" /> <input
                            type='hidden' id='sLocationId' value=''></input></td>
                        <td class="withinput" width="120px"><fmt:message
                                key="aimir.mcuid" /></td>
                        <td class="padding-r20px"><input type="text" id="sMcuName"
                            style="width: 190px;" /></td>


                        <td class="withinput"><fmt:message key="aimir.vendor" /></td>
                        <td class="padding-r20px"><select id="sVendor" name="SELECT"
                            style="width: 120px;"
                            onChange="javascript:getDeviceModelsByVenendorId();">
                                <option value="">
                                    <fmt:message key="aimir.all" />
                                </option>
                        </select></td>
                        <td class="withinput"><fmt:message key="aimir.model" /></td>
                        <td class="padding-r20px"><select id="sModel" name="select" style="width: 120px;">
                                <option value=""><fmt:message key="aimir.all" /></option>
                        </select></td>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message
                                key="aimir.installationdate" /></td>
                        <td class="padding-r20px"><span><input
                                id="sInstallStartDate" class="day" type="text"></span> <span><input
                                value="~" class="between" type="text"></span> <span><input
                                id="sInstallEndDate" class="day" type="text"></span> <input
                            id="sInstallStartDateHidden" type="hidden" /> <input
                            id="sInstallEndDateHidden" type="hidden" /></td>

                        <td class="withinput"><fmt:message key="aimir.modem" /></td>
                        <td class="padding-r20px"><select id="sModemYN" name="select" style="width: 190px;">
                                <option value=""><fmt:message key="aimir.all" /></option>
                                <option value="Y"><fmt:message key="aimir.yes" /></option>
                                <option value="N"><fmt:message key="aimir.no" /></option>
                        </select></td>
                        <td class="withinput"><fmt:message key="aimir.customer" /></td>
                        <td class="padding-r20px"><select id="sCustomerYN" name="select" style="width: 120px;">
                                <option value=""><fmt:message key="aimir.all" /></option>
                                <option value="Y"><fmt:message key="aimir.yes" /></option>
                                <option value="N"><fmt:message key="aimir.no" /></option>
                        </select></td>
                        <td class="withinput" style="width: 150px"><fmt:message
                                key="aimir.lastcomm" /></td>
                        <%-- <td colspan="1">
                            <span><input id="sLastcommStartDate" class="day"
                                type="text"></span> <span><input value="~"
                                class="between" type="text"></span>
                            <span><input id="sLastcommEndDate" class="day" type="text"></span>
                            <input id="sLastcommStartDateHidden" type="hidden"> <input
                            id="sLastcommEndDateHidden" type="hidden">
                        </td> --%>
                        <td class="padding-r20px">
                            <span><input id="sLastcommStartDate" class="day"
                                type="text"></span> <span><input value="~"
                                class="between" type="text"></span>
                            <span><input id="sLastcommEndDate" class="day" type="text"></span>
                            <input id="sLastcommStartDateHidden" type="hidden"> <input
                            id="sLastcommEndDateHidden" type="hidden">
                        </td>
                    </tr>
                    <tr>
                        <td class="withinput"><fmt:message key="aimir.customerid" /></td>
                        <td class="padding-r20px"><input id='sCustomerId'
                            type="text" style="width: 190px" /></td>
                        <td class="withinput"><fmt:message key="aimir.customername" /></td>
                        <td class="padding-r20px"><input id='sCustomerName'
                            type="text" style="width: 190px" /></td>

                        <!-- contract no -->
                        <td class="withinput"><fmt:message key="aimir.contractNumber" /></td>

                        <td class="padding-r20px"><input id='sConsumLocationId'
                            type="text" style="width: 120px" />
                        </td>
                        <td class="withinput"><fmt:message key="aimir.customeraddress" /></td>
                        <td class="padding-r20px">
                            <input id='sMeterAddress' type="text" style="width: 190px" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="8" align="right">
                            <em class="am_button">
                                <a href="javascript:reset();"><fmt:message key="aimir.form.reset"/></a>
                            </em>&nbsp;
                            <em class="am_button">
                                <a href="javascript:searchMeter()" class="on"><fmt:message key="aimir.button.search" /></a>
                            </em>
                        </td>
                    </tr>

                </table>
                <div class="clear">
                    <div id="treeDivAOuter" class="tree-billing auto"
                        style="display: none;">
                        <div id="treeDivA"></div>
                    </div>
                </div>
            </div>
        </div>
        <div id='drAlertDataPop'></div>
        <div id='drAlertDataPopFailure'></div>
        <div id='drAlert'></div>
    </form>

    <!-- Search Background (E) -->

    <div id="gadget_body">
        <div class="bodyleft_meterchart">
              <div id="fcPieChartDiv" style="margin-bottom: 7px">The chart will appear within this DIV.
                This text will be replaced by the chart.</div>
            <!-- ExtJs : meterSearchChart (S) -->
                <div id="meterSearchChart"></div>
            <!-- ExtJs : meterSearchChart (E) -->

        </div>

        <div class="bodyright_meterchart">
            <ul>
                <li class="bodyright_meterchart_leftmargin">
                    <!-- 검색결과 정렬방식 (S) --> <!-- todo: "전체" or 아래 내용을 감싸는 div필요 --> <!-- :: <fmt:message key="aimir.all"/> -->

                    <div id="search-default"
                        style="height: 26px; # margin-bottom: -2px;">
                        <ul class="search-modem">
                            <li class="withinput"><fmt:message key="aimir.filtering" /></li>
                            <li>
                                <!-- todo: 검색종류 추가  / 조회조건 --> <!--  필터링  --> <select
                                id="sCommState"  onChange="javascript:renderGrid();" Style="width: 170px;">
                                    <option value=""><fmt:message key="aimir.all" /></option>
                                    <option value="0" ><fmt:message key="aimir.commstateGreen" /></option>
                                    <option value="1"><fmt:message key="aimir.commstateYellow" /></option>
                                    <option value="2"><fmt:message key="aimir.commstateRed" /></option>
                            </select>
                            </li>
                            <li class="space10"></li>
                            <li><select id="sOrder" onChange="javascript:renderGrid();" Style="width: 210px;">
                                    <option value="1"><fmt:message key="aimir.mcu.lastcomm" /><fmt:message key="aimir.search.desc" /></option>
                                    <option value="2"><fmt:message key="aimir.mcu.lastcomm" /><fmt:message key="aimir.search.asc" /></option>
                                    <option value="3"><fmt:message key="aimir.installationdate" /><fmt:message key="aimir.search.desc" /></option>
                                    <option value="4"><fmt:message key="aimir.installationdate" /><fmt:message key="aimir.search.asc" /></option>
                            </select></li>

                            <li><a id="groupOndemandBtn" href="#" onClick="groupOndemandService();"
                                class="btn_blue"><span><fmt:message key="aimir.groupondemand" /><!-- Group Ondemand --></span></a></li>

                            <span style="float: right">
                                <li><a href="#" onClick="openExcelReport();"
                                    class="btn_blue"> <span><fmt:message
                                                key="aimir.button.excel" /></span>
                                </a></li>
                            </span>
                        </ul>

                    </div>
                    <!-- 검색결과 정렬방식 (E) -->
                    <!-- ExtJs : meterSearchGrid (S) -->
                        <div id="meterSearchGrid"></div>
                    <!-- ExtJs : meterSearchGrid (E) -->
                </li>
            </ul>
        </div>

    </div>

    <!-- Tab 1,2,3,4,5 (S) -->
    <div id="meterDetailTab">
        <ul>
            <li><a href="#general" id="_general"><fmt:message key="aimir.general" /></a></li>
            <!--  <li><a href="#meterInfo" id="_meterInfo"><fmt:message key="aimir.meter.info"/></a></li>  -->
            <li><a href="#locationInfo" id="_locationInfo"><fmt:message key="aimir.location.info" /></a></li>
            <!-- <li><a href="#history" id="_history"><fmt:message key="aimir.history"/></a></li>  -->
            <li><a href="#measurement" id="_measurement"><fmt:message key="aimir.measurement" /></a></li>
        </ul>


        <!-- Tab 1 : modemDetailTab (S) -->
        <div id="general" class="tabcontentsbox">
            <ul>
                <li>
                    <!-- search-default (S) -->
                    <div class="blueline" style="min-height: 470px; height: auto;">
                        <ul class="width" id="meterInfoArea" style="padding-bottom:330px">
                            <li class="padding">
                                <!--  JSP DIV -->
                                <div id="meterInfoDiv" class="bodyleft_meterinfo" style="height : 0"></div>
                                <!--  JSP Div 종료 -->
                                <div class="bodyright_meterinfo">
                                    <ul>
                                        <li id="meterInstallDiv"
                                            class="bodyright_meterinfo_leftmargin"></li>
                                    </ul>
                                </div>
                            </li>
                        </ul>


                        <ul class="width margin-t1px">
                            <li class="padding-bottom">
                                <div id="meterCommand">
                                    <!--  ondemand 버튼 있는 곳 --> <!-- 3rd : 명령 (S) -->
                                    <div class="headspace floatleft">
                                        <div class="floatleft margin-r5">
                                            <LABEL class="check"><fmt:message
                                                    key="aimir.instrumentation" /></LABEL>
                                        </div>
                                        <!-- 날짜 검색 조건을 받는 Ondemand 버튼 -->
                                        <div id="onDemandButton" class="floatleft margin-r5"
                                            style="display: block">
                                            <div class="floatleft">
                                                <em class="btn_org">
                                                    <a href="#this" onClick="showOnDemand();">
                                                        <fmt:message key="aimir.onDemand.Metering" />
                                                    </a>
                                                </em>
                                            </div>
                                            <div id="selectDate" class="floatleft tootipbox" style="display: none;">
                                                <span style="margin: 0px; padding: 0px;">
                                                    <button id="onDemandFromDateLeft" type="button" class="back"></button>
                                                </span>
                                                <span style="margin: 0px; padding: 0px;">
                                                    <input id="onDemandFromDate" type="text" readonly="readonly" class="day"/>
                                                    <input id="onDemandFromDateHidden" type="hidden"/>
                                                </span>
                                                <span style="margin: 0px; padding: 0px;">
                                                    <button id="onDemandFromDateRight" type="button" class="next"></button>
                                                </span>
                                                <span style="margin-left: 0px; margin-right: 0px; padding-left: 0px; padding-right: 0px;">
                                                    <input value="~" class="between" type="text"/>
                                                </span>
                                                <span style="margin: 0px; padding: 0px;">
                                                    <button id="onDemandToDateLeft" type="button" class="back"></button>
                                                </span>
                                                <span style="margin: 0px; padding: 0px;">
                                                    <input id="onDemandToDate" type="text" readonly="readonly" class="day"/>
                                                    <input id="onDemandToDateHidden" type="hidden"/>
                                                </span>
                                                <span style="margin: 0px; padding: 0px;">
                                                    <button id="onDemandToDateRight" type="button" class="next"></button>
                                                </span>
                                                <em class="am_button margin-t2px">
                                                    <a href="javascript:onDemand();"><fmt:message key="aimir.button.confirm" /></a>
                                                </em>&nbsp;
                                                <em class="am_button margin-t2px">
                                                    <a href="#this" onClick="javascript:clearAndHideOnDemandButton();"><fmt:message key="aimir.cancel" /></a>
                                                </em>
                                            </div>
                                        </div>

                                        <div id="firmWareUpdateButton" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a id='fwUpload'>Firmware Update</a>
                                            </em>
                                        </div>

                                        <div id="restoreDefaultFirmwareButton" class="floatleft margin-r5"
                                            style="display: none;">
                                            <em class="btn_org"><a
                                                href="javascript:cmdRestoreDefaultFW();">Restore Default Firmware</a></em>
                                        </div>

                                        <div id="demandResetButton" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a
                                                href="javascript:cmdDemandReset();">Demand Reset</a></em>
                                        </div>

                                        <div id="valveControlButton" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a
                                                href="javascript:cmdRelayStatus();">Meter Status</a></em> <em
                                                class="btn_org"><a href="javascript:cmdRelayActivate();">Valve
                                                    Standby</a></em> <em class="btn_org"><a
                                                href="javascript:cmdRelayOff();">Valve Off</a></em> <em
                                                class="btn_org"><a href="javascript:cmdRelayOn();">Valve
                                                    On</a></em>
                                        </div>
                                        <div id="relayActivateButton" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a href="javascript:cmdRelayActivate();">Relay Activate</a></em>
                                        </div>
                                        <div id="relayControlButton" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a href="javascript:cmdRelayStatus();">Relay Status</a></em>
                                            <em class="btn_org"><a href="javascript:cmdRelayOff();">Relay Off</a></em>
                                            <em class="btn_org"><a href="javascript:cmdRelayOn();">Relay On</a></em>
                                        </div>
                                        <div id="meterTimeSyncButton" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a
                                                href="javascript:cmdMeterTimeSync();">Meter Time Sync</a></em>
                                        </div>
                                        <div id="pingButton" class="floatleft margin-r5" style="display: none">
                                            <em class="btn_org"><a href="javascript:commandPing();">Ping</a></em>
                                        </div>
                                        <div id="tracerouteButton" class="floatleft margin-r5" style="display: none">
                                            <em class="btn_org"><a href="javascript:commandTraceroute();">Traceroute</a></em>
                                        </div>
                                        
                                        <div id="btnTOUCalendar" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a
                                                href="javascript:cmdTOUCalendar();">TOU Calendar</a></em>
                                        </div>
                                        <div id="btnDisplayItemSetting" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a
                                                href="javascript:cmdDisplayItemSetting();">Display Item Setting</a></em>
                                        </div>
                                        <div id="summerTime" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a href="javascript:cmdSummerTime();">Summer
                                                    Time</a></em>
                                        </div>
                                        <div id="energyLevel" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a
                                                href="javascript:cmdRelayStatus();">Relay Status</a></em> <em
                                                class="btn_org"><a href="javascript:cmdRelayOff();">Relay
                                                    Off</a></em> <em class="btn_org"><a
                                                href="javascript:cmdRelayOn();">Relay On</a></em>
                                        </div>
                                        <div id="meterEvent" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a
                                                href="javascript:cmdGetMeterEvent();">Get Meter Event</a></em>
                                        </div>
                                        <div id="billing" class="floatleft margin-r5"
                                            style="display: none">
                                            <em class="btn_org"><a
                                                href="javascript:cmdGetBilling();">Get Billing Register</a></em>
                                        </div>
                                    </div>

                                    <div class="meterinfo-textarea clear">
                                        <ul>
                                            <li><textarea id="commandResult" readonly>Result</textarea></li>
                                        </ul>
                                    </div>
                                    <div id="detail_dialog" class="mvm-popwin-iframe-outer" title="ondemand">
                                        <div id="detail_view" style="overflow-y: auto"></div>
                                    </div> <!-- 3rd : 명령 (E) -->
                                </div>
                            </li>
                        </ul>
                    </div> <!-- search-default (E) -->

                </li>
            </ul>
        </div>
        <!-- Tab 1 : modemDetailTab (E) -->

        <%--
        <!-- Tab 2 : meterInfo (S) -->
        <div id="meterInfo" class="tabcontentsbox" style="display: none;">
            <ul>
                <li>
                    <!-- search-default (S) -->
                    <div class="blueline" style="height: 470px;">
                        <ul class="width">
                            <li class="padding">
                                <!-- 기본정보 -->
                                <div class="headspace clear">
                                    <label class="check"><fmt:message
                                            key="aimir.button.basicinfo" /></label>
                                </div>
                                <div class="meterinfo-textarea">
                                    <ul>
                                        <li><textarea>미정의</textarea></li>
                                    </ul>
                                </div> <!-- 검침정보 -->
                                <div class="headspace_2ndline2 clear-width100 floatleft">
                                    <label class="check"><fmt:message
                                            key="aimir.button.metering" /></label>
                                </div>
                                <div class="meterinfo-textarea">
                                    <ul>
                                        <li><textarea>미정의</textarea></li>
                                    </ul>
                                </div> <!-- 기타정보 -->
                                <div class="headspace_2ndline2 clear-width100 floatleft">
                                    <label class="check"><fmt:message key="aimir.etc" /></label>
                                </div>
                                <div class="meterinfo-textarea">
                                    <ul>
                                        <li><textarea>미정의</textarea></li>
                                    </ul>
                                </div>


                            </li>
                        </ul>
                    </div> <!-- search-default (E) -->

                </li>
            </ul>
        </div>
        <!-- Tab 2 : meterInfo (E) -->
        --%>

        <!-- Tab 3 : locationInfo (S) -->
        <div id="locationInfo" class="tabcontentsbox">
            <ul>
                <li>
                    <!-- search-default (S) -->
                    <div class="blueline" style="height: 400px;">
                        <ul class="width">
                            <li class="padding">

                                <div class="map_box">
                                    <div id="map-canvas" class="border-blue-3px"
                                        style="position: absolute;"></div>
                                </div>

                                <div class="coordinate_box">
                                    <div class="width_auto padding20px"
                                        style="display: table-cell;">
                                        <form id="meterLocForm">
                                            <table width="100%" class="searching2">

                                                <caption>
                                                    <label class="check"><fmt:message
                                                            key="aimir.location.info" /></label>
                                                </caption>
                                                <tr>
                                                    <td class="padding-r20px"><fmt:message
                                                            key="aimir.latitude" /></td>
                                                    <td class="padding-r20px" align="right"><input
                                                        type="text" id="gpioX" value="${gpioX}" /></td>
                                                    <td><fmt:message key="aimir.address" /></td>
                                                </tr>
                                                <tr>
                                                    <td class="padding-r20px"><fmt:message
                                                            key="aimir.logitude" /></td>
                                                    <td class="padding-r20px" align="right"><input
                                                        type="text" id="gpioY" value="${gpioY}" /></td>
                                                    <td rowspan="2" valign="bottom"><textarea
                                                            name="customer_num3" id="sysLocation"
                                                            style="width: 260px; height: 50px;">${sysLocation}</textarea></td>
                                                </tr>
                                                <tr>
                                                    <td class="padding-r20px"><fmt:message
                                                            key="aimir.altitude" /></td>
                                                    <td class="padding-r20px" align="right"><input
                                                        type="text" id="gpioZ" value="${gpioZ}" /></td>
                                                </tr>
                                                <tr id="updLocBtnList">
                                                    <td colspan="2" align="right" class="padding-r25px"><em
                                                        class="am_button"><a
                                                            href="javascript:updateMeterLoc()" class="on"><fmt:message
                                                                    key="aimir.mcu.coordinate.update" /></a></em></td>
                                                    <td align="right"><em class="am_button"><a
                                                            href="javascript:updateMeterAddress()" class="on"><fmt:message
                                                                    key="aimir.mcu.adress.update" /></a></em> <em class="am_button"><a
                                                            href="javascript:getGeoCoding();" class="on"><fmt:message
                                                                    key="aimir.address" />-<fmt:message
                                                                    key="aimir.mcu.coordinate" /></a></em></td>
                                                </tr>
                                            </table>
                                        </form>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div> <!-- search-default (E) -->

                </li>
            </ul>
        </div>
        <!-- Tab 3 : locationInfo (E) -->

        <%--
        <!-- Tab 4 : history (S) -->
        <div id="history" class="tabcontentsbox" style="display: none;">
            <ul>
                <li>
                    <!-- search-default (S) -->
                    <div class="blueline" style="height: 650px;">

                        <div
                            class="search-bg-withouttabs margin-reset height-withouttabs-dayoptions-bt-row0">
                            <div class="dayoptions-bt searchoption-container">
                                <%@ include file="/gadget/commonDateTabButtonType2.jsp"%>
                            </div>
                        </div>

                        <ul class="width" style="padding-top: 0;">
                            <li class="padding">
                                <div class="flexlist">
                                    <div id="fcLogChartDiv" style="margin: 0 0 5px 0">The
                                        chart will appear within this DIV. This text will be replaced
                                        by the chart.</div>
                                </div>
                                <div class="flexlist">
                                    <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                                        width="100%" height="250px" id="meterLogGridEx">
                                        <param name="movie"
                                            value="${ctx}/flexapp/swf/meterLogGrid.swf" />
                                        <param name="wmode" value="opaque" />
                                        <!--[if !IE]>-->
                                        <object type="application/x-shockwave-flash"
                                            data="${ctx}/flexapp/swf/meterLogGrid.swf" width="100%"
                                            height="250px" id="meterLogGridOt">
                                            <param name="wmode" value="opaque" />
                                            <!--<![endif]-->
                                            <p>Alternative content</p>
                                            <!--[if !IE]>-->
                                        </object>
                                        <!--<![endif]-->
                                    </object>
                                </div>

                            </li>
                        </ul>
                    </div> <!-- search-default (E) -->

                </li>
            </ul>
        </div>
        <!-- Tab 4 : history (E) -->
        --%>

        <!-- Tab 5 : measurement (S) -->
        <div id="measurement" class="tabcontentsbox">
            <ul>
                <li>
                    <!-- search-default (S) -->
                    <div class="blueline" style="height: 700px;">

                        <div
                            class="search-bg-withouttabs margin-reset height-withouttabs-dayoptions-bt-row0">
                            <div class="dayoptions-bt searchoption-container">
                                <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
                            </div>
                        </div>

                        <ul class="width" style="padding-top: 0;">
                            <li class="padding" style="height: 550px;">

                                <div class="flexlist">
                                    <div id="fcMeasureChartDiv" style="margin: 0 0 5px 0">
                                        The chart will appear within this DIV. This text will be
                                        replaced by the chart.</div>
                                </div>
                                <!-- ExtJs : meterMeteringGrid (S) -->
                                    <div id="meterMeteringGrid"></div>
                                <!-- ExtJs : meterMeteringGrid (E) -->
                            </li>
                        </ul>
                    </div> <!-- search-default (E) -->

                </li>
            </ul>
        </div>
        <!-- Tab 5 : measurement (E) -->
    </div>
    <!-- Tab 1,2,3,4,5 (S) -->

</body>
</html>