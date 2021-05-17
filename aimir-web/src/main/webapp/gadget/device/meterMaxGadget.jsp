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
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet"
	type="text/css" title="blue" />

<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/public2.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/jquery-ajaxQueue.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
<script type="text/javascript" src="${ctx}/js/cluetip/jquery.cluetip.js"></script>
<script type="text/javascript"
	src="${ctx}/js/cluetip/jquery.bgiframe.min.js"></script>
<script type="text/javascript"
	src="${ctx}/js/cluetip/jquery.hoverIntent.js"></script>
<%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>

<style type="text/css">
/* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
.x-panel-bbar table {
	border-collapse: collapse;
	width: auto;
}
/* Ext-Js Grid Header style 정의. */
.x-grid3-hd-inner {
	text-align: center;
	font-weight: bold;
}

#chartPadding {
	line-height: 180px;
}
.btn_org, .btn_org a, .btn_org button, .btn_org input {
    background-repeat:  repeat-x !important;
}
</style>

<script type="text/javascript" charset="utf-8">
    // Async-Command-History Tab
	var asyncRowNo = "";
	var asyncDeviceId = "";
	var asyncTrid = "";
	var asyncState= "";
	var asyncCommand = "";
    var flexMeterLogGrid;

    var meterGridStore;
    var meterChartGrid;
    var meterChartGridStore;
    // User(Operator) Information
    var supplierId = "${supplierId}";
    supplierId = "" == supplierId ? -1 : supplierId;
    var loginId = "";
    var roleId = "";
    var protocolType = "";
    // Max Meter Limit for group command
    var maxMeters = "";

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
    var extJsTitle = '<fmt:message key="aimir.meter" />' + " " + '<fmt:message key="aimir.info" />';

    // 수정권한
    var ondemandAuth = "${ondemandAuth}";
    var editAuth = "${editAuth}";
    var cmdAuth = "${cmdAuth}";

    // EXTJS AJAX - Timeout setting (120 seconds)
    var extAjaxTimeout = 120000;

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
    var red = '#F31523'; // security error
    var orange = '#FC8F00'; // comm error
    var gray = '#BDBDBD';   //  power down
    var yellow = '#C1C115'; // NA48h
    var green = '#2A8B49' // NA24h
    var blue = '#0718D7'; // A24h
    var redbean = '#9A4A81' // unknown

    var setColorFrontTag = "<b style=\"color:";
    var setColorMiddleTag = ";\">"; 
    var setColorBackTag = "</b>";
    var targetUri = "";
    var targetType = "";
    
    var milliSecond_interval = "10000";
    
    $.ajaxSetup({
        async : false
    });

    // 유저 세션 정보 가져오기
    $.getJSON('${ctx}/common/getUserInfo.do', function(json) {
        if (json.supplierId != "") {
            loginId = json.loginId;
        }
        if ( json.roleId != ""){
        	roleId = parseInt(json.roleId);
        }
    });

    $.getJSON("${ctx}/gadget/system/user_group_max.do?param=myRoleView", { roleId: roleId } , function(json) {
    	maxMeters = 0;
    	if ( json.role != null ) {
        	if ( json.role.maxMeters != null && isNaN(json.role.maxMeters) == false )
        		maxMeters = parseInt(json.role.maxMeters);
            if ( maxMeters==0 ) // **maxMeters==0 is unlimited
                maxMeters = 2000000;
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
            $('#_schedule').bind('click', function(event) {
                var rtnBool = checkMeterId();
                // if meter is not selected, clear the page
                if(!rtnBool){
                    $("#schedule").html("No Data to Show");
                    return;
                }
                // load sub-page
                $("#schedule").load("${ctx}/gadget/device/meterMaxGadgetScheduleTab.do");
                // initialize flags : it user for 'SET' request handling.
//                mtrIntervalFlag = false;
//                retryCountFlag = false;
//                $('#intervalUpdateResult').html("Update Result : ");

            });
        });
        
        $(function() {
            $('#_measurement').bind('click', function(event) {
                if (checkMeterId()) {
                    showMeasureChart();
                }
            });
        });
        
        $(function() { $('#_asyncHistory') .bind('click',function(event) {
            modemDetailTab= "_asyncHistory";
            $("#modemDetailTabValue").val(modemDetailTab);
            getAsyncHistoryGrid();
        } ); });

        $(function() { $('#_collectMeterValues') .bind('click',function(event) {
            modemDetailTab= "_collectMeterValues";
            $("#modemDetailTabValue").val(modemDetailTab);
            
           	checkMeterId();
           	getCollectMeterValuesGrid();
        } ); });
        
        
        $(function() {
            $('#_netstationMonitoring').bind('click', function(event) {
                modemDetailTab= "_netstationMonitoring";
                $("#modemDetailTabValue").val(modemDetailTab);
                var rtnBool = checkMeterId();
                getNetstationMonitoring();
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
        $('#sMbusSMYN').selectbox();
        
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
                sMeterAddress      : condition[21],
                sGs1			   : condition[22],
                sMbusSMYN          : condition[23],
                sDeviceSerial	   : condition[24]
                
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
                      dataFild[5],
                      dataFild[6],
                      dataFild[7],
                      dataFild[8]
            ]
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
                },{
                	header: "<fmt:message key='aimir.commError'/>",
                	dataIndex: 'value4',
                	align: 'right',
                	tooltip: "<fmt:message key='aimir.commError'/>",
                	sortable: true
                },{
                	header: "<fmt:message key='aimir.securityError'/>",
                	dataIndex: 'value5',
                	align: 'right',
                	tooltip: "<fmt:message key='aimir.securityError'/>",
                	sortable: true
                },{
                    header: "<fmt:message key='aimir.powerDown'/>",
                    dataIndex: 'value6',
                    align: 'right',
                    tooltip: "<fmt:message key='aimir.powerDown'/>",
                    sortable: true
                }]
        });

        if (!meterChartGridOn) {
            meterChartGrid = new Ext.grid.GridPanel({
                width : width,
                height : 170, 
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

    function getRealtimeMeterValues(duration) {
		var timerId = 0;
		
    	// 매 10초마다 refreshGrid() 실행
    	timerId = setInterval("refreshGrid()", milliSecond_interval);
    	
    	// duration 경과, refresh 함수 종료
    	setTimeout("stopRefresh(" + timerId + ")", 60000 * duration); 
	}
    
    function refreshGrid() {
    	getCollectMeterValuesGrid();
    }
    
    function stopRefresh(timerId) {
    	clearInterval(timerId);
    }
    
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
                sMeterAddress      : condition[21],
                sHwVersion         : "",
                sFwVersion         : "",
                sGs1         : condition[22],
                sMbusSMYN          : condition[23],
                sDeviceSerial	   : condition[24],
                sType : '',
                fwGadget : 'N'
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
                { name: 'modemId', type: 'string' },
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
                { name: 'locId', type: 'string' },
                { name: 'activityStatus', type: 'string'} ,
                { name: 'gs1', type: 'string'}
            ]
        });

        meterGridColModel = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true,
                menuDisabled: true
            },
            columns: [{
            	header: "<div class='am_button' style='background:none'><input type='checkbox' id='allCheck' onClick='chkAll()' /></div>",
            	width: 30,
            	align:'center',
            	renderer: dataChk
            	
            },{
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
                width: 115,
                sortable: true,
                renderer : function(value, me, record, rowNumber, columnIndex, store) {  //ibk
                    if(record.data.commStatusByCode == "1.3.3.14") //CommError
                    	return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
                    if(record.data.commStatusByCode == "1.3.3.13") //SecurityError
                        return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
                    if(record.data.commStatusByCode == "1.3.3.5") //PowerDown
                        return setColorFrontTag + gray + setColorMiddleTag + value + setColorBackTag;
                    if(record.data.activityStatus == "A24h")
                    	return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
                    else if(record.data.activityStatus == "NA24h")
                    	return setColorFrontTag + green + setColorMiddleTag + value + setColorBackTag;
                    else if(record.data.activityStatus == "NA48h")
                    	return setColorFrontTag + yellow + setColorMiddleTag + value + setColorBackTag;
                    else //unknown
                    	return setColorFrontTag + redbean + setColorMiddleTag + value + setColorBackTag;
                }
            },{
                header: "<fmt:message key='aimir.shipment.gs1'/>",
                dataIndex: 'gs1',
                align:'center',
                width: 115,
                sortable: true,
                renderer : function(value, me, record, rowNumber, columnIndex, store) {  //ibk
                    if(record.data.commStatusByCode == "1.3.3.14") //CommError
                    	return setColorFrontTag + orange + setColorMiddleTag + value + setColorBackTag;
                    if(record.data.commStatusByCode == "1.3.3.13") //SecurityError
                        return setColorFrontTag + red + setColorMiddleTag + value + setColorBackTag;
                    if(record.data.commStatusByCode == "1.3.3.5") //PowerDown
                        return setColorFrontTag + gray + setColorMiddleTag + value + setColorBackTag;
                    if(record.data.activityStatus == "A24h")
                    	return setColorFrontTag + blue + setColorMiddleTag + value + setColorBackTag;
                    else if(record.data.activityStatus == "NA24h")
                    	return setColorFrontTag + green + setColorMiddleTag + value + setColorBackTag;
                    else if(record.data.activityStatus == "NA48h")
                    	return setColorFrontTag + yellow + setColorMiddleTag + value + setColorBackTag;
                    else //unknown
                    	return setColorFrontTag + redbean + setColorMiddleTag + value + setColorBackTag;
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
                       /*  case "fmtMessage00": return fmtMessage[0]; break;
                        case "fmtMessage24": return fmtMessage[1]; break;
                        case "fmtMessage48": return fmtMessage[2]; break; */
                        case "1.3.3.8": return setColorFrontTag + skyBlue + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break; //NEW_REGISTERED
                        case "1.3.3.1" : return setColorFrontTag + purple + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break; // NORMAL
                        case "1.3.3.4" : return setColorFrontTag + red + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break; // CUT_OFF
                        //ibk
                        case "1.3.3.14" : return setColorFrontTag + orange + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break; // CommError
                        case "1.3.3.13" : return setColorFrontTag + red + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break; // SecurityError
                        case "1.3.3.5" : return setColorFrontTag + gray + setColorMiddleTag + record.data.commStatus + setColorBackTag ; break; // PowerDown
                        default : return  setColorFrontTag + redbean + setColorMiddleTag + record.data.commStatus + setColorBackTag ;

                    }
                }
            },{
                header: "<fmt:message key='aimir.mcuid'/>",
                dataIndex: 'mcuSysID',
                align:'center',
                width: 75,
                sortable: true
            },{
                header: "<fmt:message key='aimir.modemid'/>",
                dataIndex: 'modemId',
                align:'center',
                width: 90,
                sortable: true,
                renderer : addTooltip
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
                header: "<fmt:message key="aimir.install"/> "+"<fmt:message key="aimir.id"/>",
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
                height : 360,
                store : meterGridStore,
                colModel : meterGridColModel,
                selModel : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners : {
                        rowselect : function(selectionModel, columnIndex, value) {
                        	$("#meterDetailTab").show();
                            var param = value.data;
                            setMeterId(param.meterId, param.meterMds, param.meterType, param.mcuSysID);
                            setModemInfo(param.meterId);
                            tempDeviceId=param.meterMds;
                            tempModelName=param.modelName;
                            tempLocId = param.locId;
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

 	function dataChk(currentCellValue, metadata, record, rowIndex, colIndex) {
		var chkHtml = "<div class=\"am_button\" style=\"background:none\">" +
                "<input type=\"checkbox\" id=\"chkMeterId\" name=\"chkMeterId\" value=\"" +
                record.data.meterId + "," + record.data.meterMds + "," +
                record.json.modemId + "," + record.data.mcuSysID + ","+ record.data.no +"\" /></div>";
        return chkHtml;
	}
 	
 	function chkAll() {
 		if ($("#allCheck").is(':checked')) {
 			$("input[name='chkMeterId']").attr("checked", "checked");
 		} else {
 		    $("input[name='chkMeterId']").attr("checked", false);
 		}
 	}
 	
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
/*         if (ondemandAuth == "true") {
            $("#groupOndemandBtn").show();
            $("#onDemandButton").show();
        } else {
            $("#groupOndemandBtn").hide();
            $("#onDemandButton").hide();
        } */

        if (editAuth == "true") {
            $("#updLocBtnList").show();
        } else {
            $("#updLocBtnList").hide();
        }

        if (ondemandAuth != "true" && cmdAuth != "true") {
            $("#meterCommand").hide();
        }

        var locDateFormat = "yymmdd";

	    $("#asyncCommandHistoryStartDate") .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst);}} );
        $("#asyncCommandHistoryEndDate")   .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst);}} );
        $("#collectMeterValuesStartDate") .datepicker({showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDateLocal(dateText, inst);}} );
        
        var date = new Date();
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();


        if(("" + month).length == 1) month = "0" + month;
        if(("" + day).length == 1) day = "0" + day;

        var setDate      = year + "" + month + "" + day;
        var dateFullName = "";

        // 날짜를 국가별 날짜 포맷으로 변경
        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    dateFullName = json.localDate;
                    $("#asyncCommandHistoryStartDate").val(dateFullName);
                    $("#asyncCommandHistoryEndDate").val(dateFullName);
                    $("#collectMeterValuesStartDate").val(dateFullName);
                });

        $("#asyncCommandHistoryStartDateHidden").val(setDate);
        $("#asyncCommandHistoryEndDateHidden").val(setDate);
        $("#collectMeterValuesStartDateHidden").val(setDate);
	    
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
        $("#meterDetailTab").hide();
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

    var mcuId = ''; // 선택된 mcuId
    var meterId = ''; // 선택된 meterId
    var meterType = ''; // 선택된 meterType
    var meterMds = ''; // 선택된 meterMds
    var logGrid = ''; // 선택된 logGrid

    var modemType = "";
    var modemProtocolType = "";
    
    function getMeterMds() {
        return meterMds;
    }
    function getMeterId() {
        return meterId;
    }

    // 미터 상세조회
    function setMeterId(gridMeterId, gridMeterMds, gridMeterType, gridMcuId) {
        var tabs_selectIndex = $('#meterDetailTab').tabs().tabs('option',
                'selected');

        mcuId = gridMcuId;
        meterId = gridMeterId;
        meterMds = gridMeterMds;
        meterType = gridMeterType;
        getMeter();

        frUpload._settings.data.modemId = $('#modemId').val();
        frUpload._settings.data.meterId = meterId;

        viewMeterMap();
        showMeasureChart();
    }
    
    function setModemInfo(gridMeterId) {
    	//비동기 설정
        $.ajaxSetup({
            async : true
        });
        $.getJSON('${ctx}/gadget/device/getModemByMeter.do', {
            'meterId' : meterId
        }, function(data) {
        	modemType = data.modemType;
        	modemProtocolType = data.protocolType;
        });
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
        arrayObj[22] = $('#sGs1').val();
        arrayObj[23] = $('#sMbusSMYN').val(); //SP-929
        arrayObj[24] = $('#sDeviceSerial').val();
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

    //////////////////////////////////////////////////////////////////////
	// Source code of Group Comannds were moved to meterGroupCommand.jsp//
    //////////////////////////////////////////////////////////////////////

    // Group Command Popup - han sejin
    var groupCommandWin;
    function groupCommandService(){
        if(groupCommandWin){
            groupCommandWin.close();
        }

        if(cmdAuth=='true'){
            var opts = "width=1400, height=870, left=100px, top=100px  resizable=no, status=no";
            var grpObj = new Object();
            grpObj.searchCondition =  getCondition();
            grpObj.checkedItem = getChekedMeterList();
            grpObj.supplierId = supplierId;
            grpObj.roleId = roleId;
            grpObj.loginId = loginId;

            groupCommandWin = window.open("${ctx}/gadget/device/meterMaxGroupCommandPopup.do",
                    "meterMaxGadgetGroupCommand", opts);
            groupCommandWin.opener.obj = grpObj;
        }else{
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.firmware.msg02"/>');
        }
    }

    // Group Command - get checked meters
    function getChekedMeterList() {
		var loopSize = $("input[name='chkMeterId']").length;
        var meterIdList = new Array();
        var k = 0;
        for ( var i = 0; i < loopSize ;i++ ){
            if(loopSize > 1){
                if ( chkMeterId[i].checked == true )	meterIdList[k++] = chkMeterId[i].value.split(',');
            }else{
        		if ( chkMeterId.checked == true )		meterIdList[k++] = chkMeterId.value.split(',');
            }
        }
        return meterIdList;
    }

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
		//ibk
        var dataFild = new Array();
        dataFild[0] = "no";
        dataFild[1] = "mcuSysID";
        dataFild[2] = "value0";
        dataFild[3] = "value1";
        dataFild[4] = "value2";
        dataFild[5] = "value3";
        dataFild[6] = "value4";
        dataFild[7] = "value5";
		dataFild[8] = "value6";
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
    
    function modmPortCheck(port){
    	if ( port == null || port == ""){
    		return true;
    	}
    	if(isNaN(port)){
    		return false;
    	}
    	var portNum = parseInt(port);
    	if ( 0 <= portNum && portNum <= 4 ){
    		return true;
    	}
    	return false;
    }
    
    function checkDeviceLicence() {
    	var licenceCount = 0;
    	
    	$.getJSON('${ctx}/gadget/device/eventAlert/checkDeviceLicence.do', {
			'supplierId' : supplierId,
			'activatorTypeId' : $('#meterType').val(),
		}, function(data) {
			licenceCount = data.checkResult;
		});
    	
    	return licenceCount;
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
        
        if(checkDeviceLicence() != 0) {
        	Ext.Msg.alert('<fmt:message key='aimir.message'/>','Excessive Number of Device Registration. (limited quantity : ' + checkDeviceLicence() + ')');
        	return;
        }

        if (!nullCheck(checkList))
            return;

        if (!doubleCheck($('#usageThreshold').val())) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.threshold.number"/>');
            return;
        }

        if ( !modmPortCheck($('#modemPort').val())){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.mbusaddress.number"/>');
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
            $("#meterInfoFormEdit :input[id='modemPortHidden']").val(
                    $('#modemPort').val());
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
        $("#meterInfoFormEdit :input[id='modemPortHidden']").val(
                $('#modemPort').val());
        
        $("#meterInfoFormEdit :input[id='supplierId']").val(supplierId);

        if (!doubleCheck($('#usageThreshold').val())) {
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.threshold.number"/>');
            return;
        }
        if ( !modmPortCheck($('#modemPort').val())){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.mbusaddress.number"/>');
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
                'installDate' : $('#installDate').val(),
                'modemPort' : $('#modemPort').val()
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
        setMeterId(meterId, meterMds, meterType, mcuId);
    }

    // grid 의 store 만 reload
    function reloadMeter() {
        updateFChart();
        meterChartGridStore.reload(meterChartGridStore.lasgOptions);
        meterGridStore.reload(meterGridStore.lasgOptions);
    }

    // 미터 기본 삭제 처리 
    var deleteMeterInfo = function() {
    	DeleteMeterPanel();
    };
    
    var captchacount=1;	//틀린 횟수 체크
    var incorrectCodeCheck = false; //틀렸을 경우 메세지를 보여주기 위해서
 	function CaptchaPanel(option){
 		
    	// 아직 안닫힌 경우 기존 창은 닫기
 		if(Ext.getCmp('captchaWindowPanel')){
 			Ext.getCmp('captchaWindowPanel').close();
 		} 		
 		
 		var captchaFormPanel =  new Ext.form.FormPanel({ 		      		         		       
 		        id          : 'formpanel',
 		        defaultType : 'fieldset', 		 
 		        bodyStyle:'padding:1px 1px 1px 1px',
 		        frame       : true,
 		        items       : [
 		            {
 		            	xtype: 'panel',
 		            	html: '<center><img src="${ctx}/CaptChaImg.jsp?rand='+ Math.random() + '"/></center></br>',
 		            	align:'left'
 		            	
 		            },
 		            {
 		            	xtype: 'textfield',
 		            	id : 'captchaCode',
 		            	fieldLabel: '<fmt:message key="aimir.captchaCode" />',
 		                emptyText: '<fmt:message key="aimir.enterTheCode" />',
 		                disabled : false,
 		               
 		            },
 		           {
 		            	xtype: 'label',
 		            	id : 'infolabel',
 		            	style : {
 		            		background : '#ffff00'
 		            	},
 		            	text : '*<fmt:message key="aimir.incorrectCode" />',
 		            	hidden: true
 		            }

 		        ],
 		        buttons: [
 		            {
			    	 	text: '<fmt:message key="aimir.refresh" />',
			    	 	listeners: {
			            	click: function(btn,e){
			            		captchaWindow.load(CaptchaPanel(option));
			            		
			            	}
			            }
			        },{
			            text: '<fmt:message key="aimir.submit" />',
			            listeners: {
			            	click: function(btn,e){
			            		if(5==captchacount){
  			              		  window.open('${ctx}/admin/logout.do',"_parent").parent.close();
  			              	  	} 
			            		$.ajax({
			                        url: '${ctx}/gadget/report/CaptchaSubmit.do',
			                        type: 'POST',
			                        dataType: 'json',
			                        data: 'answer=' + $('#captchaCode').val(),
			                        async: false,  
			                        success: function(data) {
			                             if(data.capcahResult=="true"){
			                            	//올바른 코드 입력 시
			                            	 captchacount = 1;
			                            	if(option == "on"){
			                            		Ext.getCmp('captchaWindowPanel').close();
			                            		cmdRelayOn();
			                            	}else if(option == "off"){
			                            		Ext.getCmp('captchaWindowPanel').close();
			                            		cmdRelayOff();
			                            	}else if(option == "delete"){
			                            	 captchaWindow.load(DeleteMeterPanel()); //삭제화면 로딩
			                            	}else if(option == "limitPowerUsage"){
                                                Ext.getCmp('captchaWindowPanel').close();
                                                cmdLimitPowerUsage();
                                            }

			                             }else{  
			                            	 //잘못된 코드 입력
			                            	 captchacount++;
			                            	 incorrectCodeCheck = true;
			                            	 captchaWindow.load(CaptchaPanel(option));
			                             }
			                       		}
			                  		});
			            	}
			            }
			        },{
			            text: '<fmt:message key="aimir.cancel" />',
		            	listeners: {
	                        click: function(btn,e) {
	                        	Ext.getCmp('captchaWindowPanel').close();
	                        }
	                    }
		        }]
 		    });
 			var msg="";
 			if(option =="on")
 				msg = "Relay On";
 			else if(option =="off")
 				msg = "Relay Off";
 			else if(option == "delete")
 				msg = '<fmt:message key="aimir.confirmMeterDelete" />'
 		    var captchaWindow = new Ext.Window({
 		        id     : 'captchaWindowPanel',
 		        title  : msg,
 		        pageX : $("#general").width()/2-300,
                pageY : 600,
 		        height : 206, 
 		        width  : 300,
 		        layout : 'fit',
 		        bodyStyle   : 'padding: 10px 10px 10px 10px;',
 		        items  : [captchaFormPanel],
 		        resizable: false
 		    });
 		    
 		    captchaWindow.show();
 			// 코드가 틀렸을 경우 메세지가 보이게
     		if(incorrectCodeCheck == true){
     			Ext.getCmp('infolabel').setVisible(true);
     			Ext.getCmp('captchaCode').focus(true,100);
     			incorrectCodeCheck = false;	
     		}
 	}


 	
 	function DeleteMeterPanel(){
 		// 아직 안닫힌 경우 기존 창은 닫기
 		if(Ext.getCmp('deleteMeterWindowPanel')){
 			Ext.getCmp('deleteMeterWindowPanel').close();
 		} 		
 		
 		//Ext.getCmp('captchaWindowPanel').close();
 		var deleteMeterFormPanel =  new Ext.form.FormPanel({ 		      		         		       
 		        id          : 'formpanel',
 		        defaultType : 'fieldset', 		 
 		        bodyStyle:'padding:1px 1px 1px 1px',
 		        frame       : true,
 		        labelWidth  : 80, 		        
 		        items       : [
 		            {
 		            	xtype: 'label',
 		            	id : 'infolabel',
 		            	style : {
 		            		background : '#ffff00'
 		            	} ,
 		            	text : '<fmt:message key="aimir.msg.wantdelete" />'
 		            }
 		            
 		        ],
 		        buttons: [
 		            {
			            text: 'YES',
			            listeners: {
			            	click: function(btn,e){
			            		$.ajax({
			        	            type : "POST",
			        	            data : {
			        	            	'id' : getMeterId()
			        	            },
			        	            dataType : "json",
			        	            url : '${ctx}/gadget/device/delteMeter.do',
			        	            success : deleteMeterInfoResult,
			        	            error : function(request, status) {
			        	                //Ext.Msg.alert("meter delete ajax comm failed");
			        	                var msg = Ext.Msg.show({
	                            			   title:'<fmt:message key='aimir.message'/>',
	                            			   msg: "meter delete ajax comm failed",
	                            			   buttons: Ext.Msg.OK,
	                            		       cls: 'msgbox',
	                            	           
	                            			});
		                            		msg.getDialog().setPosition($("#general").width()/2-600,600);
			        	            }
			        	        });
			            		
			            		Ext.getCmp('deleteMeterWindowPanel').close();
	                        	//Ext.getCmp('captchaWindowPanel').close();
			            }}
			        },{
			            text: 'NO',
		            	listeners: {
	                        click: function(btn,e) {
	                        	Ext.getCmp('deleteMeterWindowPanel').close();
	                        	//Ext.getCmp('captchaWindowPanel').close();
	                        }
	                    }
		        }]
 		});
 		    var deleteMeterWindow = new Ext.Window({
 		        id     : 'deleteMeterWindowPanel',
 		        title  : '<fmt:message key="aimir.confirmMeterDelete" />',
 		        pageX : $("#general").width()/2-600,
                pageY : 600,
 		        height : 140,
 		        width  : 300,
 		        layout : 'fit',
 		        bodyStyle   : 'padding: 10px 10px 10px 10px;',
 		        items  : [deleteMeterFormPanel],
 		    });
 		   deleteMeterWindow.show();
 	}
	
    // 미터 삭제 후 처리
    function deleteMeterInfoResult(responseText, status) {
        if(responseText.id == null) {
            var tempMsg = '<fmt:message key="aimir.msg.deleteFail" />';
            /* Ext.Msg.alert(extJsTitle, $('#mdsId').val() + ": "
                    + tempMsg.substring(tempMsg.lastIndexOf("_") + 1), null, null); */
            var msg = Ext.Msg.show({
 			   title:'<fmt:message key='aimir.message'/>',
 			   msg: $('#mdsId').val() + ": "
               + tempMsg.substring(tempMsg.lastIndexOf("_") + 1),
 			   buttons: Ext.Msg.OK,
 		       cls: 'msgbox',
 			});
     		msg.getDialog().setPosition($("#general").width()/2-600,600);
        } else {
            var tempMsg = ':Meter <fmt:message key="aimir.msg.deletesuccess" />';
            /* Ext.Msg.alert(extJsTitle, $('#mdsId').val() + " "
                    + tempMsg.substring(tempMsg.lastIndexOf("_") + 1), null, null); */
            var msg = Ext.Msg.show({
  			   title:'<fmt:message key='aimir.message'/>',
  			   msg: $('#mdsId').val() + " "
               + tempMsg.substring(tempMsg.lastIndexOf("_") + 1),
  			   buttons: Ext.Msg.OK,
  		       cls: 'msgbox',
  			});
      		msg.getDialog().setPosition($("#general").width()/2-600,600);
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
        $("#meterInstallDiv").load("${ctx}/gadget/device/meterMaxGadgetEmptyMeter.do");

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
        $('#relayActivateButton').hide();
        // $('#meterTimeSyncButton').hide();

        // 명령 버튼의 사용 가능 여부 판단.
        enableButton(modelId);
       
    }

    // 미터 모델에 따라 표시될 버튼을 결정한다.
    var enableButton = function(modelId) {
    	//result 창 초기화
        $('#commandResult').val('');

        //기존 버튼 초기화
        $('#relayActivateButtion').hide();
        // $('#meterTimeSyncButton').hide();
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
        //$('#relayControlButton').hide(); -- seperate to 3 div from 1 div(status+off+on)
    	$('#relayActivateButton').hide();
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
    	$('#coapPingButton').hide();
    	$('#limitPowerUsageButton').hide();
    	
        $('#OBISButton').hide();
        $('#OBISButton').hide();
        $('#cmdRelayOffButton').hide();
        $('#cmdRelayOnButton').hide();
        $('#cmdRelayStatusButton').hide();
        $('#onDemandButton').hide();
        $('#meterTimeSyncButton').hide();
        $('#getTariff').hide();
        $('#setTariff').hide();
/*         $('#getCurrnetLoadLimit').hide();
        $('#setCurrnetLoadLimit').hide(); */
        $('#otaButton').hide();
        $('#fwVersionButton').hide();
    	
    	 // Command 권한 체크
        if (cmdAuth != "true") {
            return;
        }
    	//
        if(modelId=="" || modelId == null)
        	return;
    	
        
        //DeviceModelController.java
        $.getJSON('${ctx}/gadget/system/modelinfo2.do', {
            devicemodelId : modelId,
            supplierId : supplierId,
            roleId : roleId
        }, function(json) {

            if (json.namesOfContain.length > 0) {
                // 해당 모델이 사용 가능한 명령어 목록을 읽어와 버튼을 표시한다.
                // 모델목록과 명령은 CommonConstants.java Enum by EnableCommandModel 에 정의 되어있다.
                // **해당 role의 read/write 권한을 연계하여 write관련 버튼은 표시제어함.(editAuth)
                for ( var i = 0; i < json.namesOfContain.length; i++) {
                    switch (json.namesOfContain[i]) {
	                    case 'OBIS Get':
	                        $('#OBISButton').show();
	                        break;
	                    case 'OBIS Set':
	                    	if (editAuth == "true")
	                        	$('#OBISButton').show();
	                        break;
                        case 'Relay Off':
                            if (editAuth == "true")
                                $('#cmdRelayOffButton').show();
                            break;
                        case 'Relay On':
                            if (editAuth == "true")
                                $('#cmdRelayOnButton').show();
                            break;
                        case 'Relay Status':
                            $('#cmdRelayStatusButton').show();
                            break;
                        case 'On Demand Metering':
                            $('#onDemandButton').show();
                            break;
                        case 'Meter Time Synchronization':
                            $('#meterTimeSyncButton').show();
                            break;
                        case 'Get Tariff':
                            $('#getTariff').show();
                            break;
                        case 'Set Tariff':
                        	if(editAuth == "true")
                            	$('#setTariff').show();
                            break;
/*                         case 'Get Current Load Limit':
                            $('#getCurrnetLoadLimit').show();
                            break;
                        case 'Set Current Load Limit':
                            $('#setCurrnetLoadLimit').show();
                            break; */
                        case 'Meter OTA':
                            if(editAuth == "true")
                                $('#otaButton').show();
							break;
                        case 'Get Meter F/W Version':
                            $('#fwVersionButton').show();
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
    	//비동기 설정
        $.ajaxSetup({
            async : true
        });
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
                params, function(response, status, xhr){
        	if(status=='error' && response.includes('More than one row with the given identifier was found')){
        		Ext.Msg.alert('<fmt:message key='aimir.message'/>','Please check if the same meter is connected to another contract in duplicate.');
        	}
/*             console.log("qwerqwerqwer");
            console.log(response);
            console.log(status);
            console.log(xhr); */
        });
        getAsyncHistoryGrid();
        
        getCollectMeterValuesGrid();
        getNetstationMonitoring();
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
        
        var head = document.getElementsByTagName('head')[0];
        var script = document.createElement('script');
        script.type = 'text/javascript';
        script.charset = 'utf-8';
        script.src = '${ctx}/js/googleMap.jsp';

        head.appendChild(script);
    }

    // Schedule Tab : NI Command, RetryCount [GET]
    // retryCountFlag used to flag. (GET action shold success before SET action)
    var retryCountFlag = false;
    function getRetryCountAction(){
        // Check whether or not modem is connected
        var tModemId = $('#modemId').val();
        if(tModemId == undefined || "" == tModemId){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.modemNotConnected"/>');
            return false;
        }

        // Ajax Definition
        Ext.define('retryCountAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    retryCountFlag = false;
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

        // Ajax Call (timeout : 180 seconds -> important)
        retryCountAjax.request({
            url : "${ctx}/gadget/device/command/cmdRetryCount.do",
            method : 'POST',
            timeout : extAjaxTimeout,
            params: {
                modemId : tModemId,
                loginId : loginId,
                requestType : 'GET',
                requestValue : '0'
            },
            success: function (result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                var jsonData = Ext.util.JSON.decode( result.responseText );
                $('#retryComment').html(" "+jsonData.cmdResult);
                $('#retryCountInput').val(jsonData.retryCount);
                // Set true when retryCount is comming
                if(jsonData.status != undefined && jsonData.status.toUpperCase()=='SUCCESS'){
                    retryCountFlag = true;
                }else{
                    $('#retryComment').html(" "+jsonData.rtnStr);
                }
            },
            failure: function(result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                retryCountFlag = false;
                if(result.isTimeout){
                    $('#retryComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                }else{
                    $('#retryComment').html(" "+request.statusText);
                }
            }
        });
    }

    // Schedule Tab : NI Command, RetryCount [SET]
    function setRetryCountAction() {
        // Check again whether or not modem is connected
        var tModemId = $('#modemId').val();
        if(tModemId == undefined || "" == tModemId){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.modemNotConnected"/>');
            return false;
        }
        // GET Command should be success before SET Command
        if(!retryCountFlag){
            // [GET] first, then update function is possible.
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','Check the current value by GET button before update');
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
                            // Ajax Call (timeout : 180 seconds -> important)
                            retryCountAjax.request({
                                url : "${ctx}/gadget/device/command/cmdRetryCount.do",
                                method : 'POST',
                                timeout : extAjaxTimeout,
                                params: {
                                    modemId : tModemId,
                                    loginId : loginId,
                                    requestType : 'SET',
                                    requestValue : retryInput
                                },
                                success: function (result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    retryCountFlag = true;
                                    var jsonData = Ext.util.JSON.decode( result.responseText );
                                    $('#retryComment').html(" "+jsonData.cmdResult);
                                    $('#retryCountInput').val(jsonData.retryCount);
                                    if(jsonData.cmdResult == undefined ){
                                        $('#retryComment').html(" "+jsonData.rtnStr);
                                    }
                                },
                                failure: function(result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    retryCountFlag = false;
                                    if(result.isTimeout){
                                        $('#retryComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                                    }else{
                                        $('#retryComment').html(" "+request.statusText);
                                    }
                                }
                            });
                        } //~confirm inner function
                    }); //~Ext.Msg.confirm
        } //~validation check

    }

    // Schedule Tab : NI Command, Metering Interval
    // mtrIntervalFlag is used to flag. (GET action shold success before SET action)
    var mtrIntervalFlag = false;
    function getMtrIntervalAction(){
        // Check whether or not modem is connected
        var tModemId = $('#modemId').val();
        if(tModemId == undefined || "" == tModemId){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.modemNotConnected"/>');
            return false;
        }
        // Ajax Definition
        Ext.define('mtrIntervalAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    mtrIntervalFlag = false;
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

        // Ajax Call (timeout : 180 seconds -> important)
        mtrIntervalAjax.request({
            url : "${ctx}/gadget/device/command/cmdMeteringInterval.do",
            method : 'POST',
            timeout : extAjaxTimeout,
            params: {
                modemId : tModemId,
                loginId : loginId,
                requestType : 'GET',
                requestValue : '0'
            },
            success: function (result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                var jsonData = Ext.util.JSON.decode( result.responseText );
                $('#intervalComment').html(" "+jsonData.cmdResult);
                // Set true when retryCount is comming
                if(jsonData.status != undefined && jsonData.status.toUpperCase()=='SUCCESS'){
                    mtrIntervalFlag = true;
                    var mtrIntervalArray = (jsonData.interval).split(':');
                    $('#intervalHour').val(mtrIntervalArray[0]);
                    $('#intervalMinute').val(mtrIntervalArray[1]);
                    $('#intervalSecond').val(mtrIntervalArray[2]);
                }else{
                    $('#intervalComment').html(" "+jsonData.rtnStr);
                }
            },
            failure: function(result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                mtrIntervalFlag = false;
                if(result.isTimeout){
                    $('#intervalComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                }else{
                    $('#intervalComment').html(" "+request.statusText);
                }
            }
        });
    }

    // Metering Interval Action [SET]
    function setMtrIntervalAction(){
        // Check again whether or not modem is connected
        var tModemId = $('#modemId').val();
        if(tModemId == undefined || "" == tModemId){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.modemNotConnected"/>');
            return false;
        }
        // GET Command should be success before SET Command
        if(!mtrIntervalFlag){
            // [GET] request should success first, then update function is possible.
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','Check the current value by GET button before update');
            return false;
        }
        // User Input
        var itvHour = $('#intervalHour').val().trim();
        var itvMin = $('#intervalMinute').val().trim();
        var itvSec = $('#intervalSecond').val().trim();
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
                            // Ajax Call (timeout : 60 seconds -> important)
                            mtrIntervalAjax.request({
                                url : "${ctx}/gadget/device/command/cmdMeteringInterval.do",
                                method : 'POST',
                                timeout : extAjaxTimeout,
                                params: {
                                    modemId : tModemId,
                                    loginId : loginId,
                                    requestType : 'SET',
                                    requestValue : parseInt(itvTotal)
                                },
                                success: function (result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    mtrIntervalFlag = true;
                                    var jsonData = Ext.util.JSON.decode( result.responseText );
                                    $('#intervalComment').html(" "+jsonData.cmdResult);
                                    $('#intervalUpdateResult').html("Update Result : " + jsonData.intervalStatus);
                                    if(jsonData.cmdResult == undefined ){
                                        $('#intervalComment').html(" "+jsonData.rtnStr);
                                    }
                                },
                                failure: function(result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    mtrIntervalFlag = false;
                                    if(result.isTimeout){
                                        $('#intervalComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                                    }else{
                                        $('#intervalComment').html(" "+request.statusText);
                                    }
                                }
                            });
                        } //~confirm inner function
                    }); //~Ext.Msg.confirm
        } //~validation check

    } //~function setMtrIntervalAction

    // Schedule Tab : NI Command, Transmit Frequency
    // trsFrequencyFlag is used to flag. (GET action shold success before SET action)
    var trsFrequencyFlag = false;
    function getTrsFrequencyAction(){
        // Check whether or not modem is connected
        var tModemId = $('#modemId').val();
        if(tModemId == undefined || "" == tModemId){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.modemNotConnected"/>');
            return false;
        }
        // Ajax Definition
        Ext.define('trsFrequencyAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    trsFrequencyFlag = false;
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

        // Ajax Call (timeout : 180 seconds -> important)
        trsFrequencyAjax.request({
            url : "${ctx}/gadget/device/command/cmdTransmitFrequency.do",
            method : 'POST',
            timeout : extAjaxTimeout,
            params: {
                modemId : tModemId,
                loginId : loginId,
                requestType : 'GET',
                second : '0'
            },
            success: function (result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                var jsonData = Ext.util.JSON.decode( result.responseText );
                $('#transmitComment').html(" "+jsonData.cmdResult);
                // Set true when frequency is comming
                if(jsonData.status != undefined && jsonData.status.toUpperCase()=='SUCCESS'){
                    trsFrequencyFlag = true;
                    var trsFrequencyArray = (jsonData.frequency).split(':');
                    $('#transmitHour').val(trsFrequencyArray[0]);
                    $('#transmitMinute').val(trsFrequencyArray[1]);
                    $('#transmitSecond').val(trsFrequencyArray[2]);
                }else{
                    $('#transmitComment').html(" "+jsonData.rtnStr);
                }
            },
            failure: function(result, request){
                //폼 윈도우를 닫고, 결과 처리
                Ext.MessageBox.hide();
                trsFrequencyFlag = false;
                if(result.isTimeout){
                    $('#transmitComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                }else{
                    $('#transmitComment').html(" "+request.statusText);
                }
            }
        });
    }

    // Transmit Frequency Action [SET]
    function setTrsFrequencyAction(){
        // Check again whether or not modem is connected
        var tModemId = $('#modemId').val();
        if(tModemId == undefined || "" == tModemId){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.modemNotConnected"/>');
            return false;
        }
        // GET Command should be success before SET Command
        if(!trsFrequencyFlag){
            // [GET] request should success first, then update function is possible.
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','Check the current value by GET button before update');
            return false;
        }
        // User Input
        var frqHour = $('#transmitHour').val().trim();
        var frqMin = $('#transmitMinute').val().trim();
        var frqSec = $('#transmitSecond').val().trim();
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
                            // Ajax Call (timeout : 60 seconds -> important)
                            trsFrequencyAjax.request({
                                url : "${ctx}/gadget/device/command/cmdTransmitFrequency.do",
                                method : 'POST',
                                timeout : extAjaxTimeout,
                                params: {
                                    modemId : tModemId,
                                    loginId : loginId,
                                    requestType : 'SET',
                                    second : parseInt(frqTotal)
                                },
                                success: function (result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    trsFrequencyFlag = true;
                                    var jsonData = Ext.util.JSON.decode( result.responseText );
                                    $('#transmitComment').html(" "+jsonData.cmdResult);
                                    $('#transmitUpdateResult').html("Update Result : " + jsonData.transmitStatus);
                                    if(jsonData.cmdResult == undefined ){
                                        $('#transmitComment').html(" "+jsonData.rtnStr);
                                    }
                                },
                                failure: function(result, request){
                                    //폼 윈도우를 닫고, 결과 처리
                                    Ext.MessageBox.hide();
                                    trsFrequencyFlag = false;
                                    if(result.isTimeout){
                                        $('#transmitComment').html(" "+"Response Timeout ("+request.timeout/1000+"s)");
                                    }else{
                                        $('#transmitComment').html(" "+request.statusText);
                                    }
                                }
                            });
                        } //~confirm inner function
                    }); //~Ext.Msg.confirm
        } //~validation check

    } //~function setTrsFrequencyAction

    // Bypass -- Limit Power Usage (0.0.17.0.0.255)
    function cmdLimitPowerUsage() {
        // Check whether or not modem is connected
        var tModemId = $('#modemId').val();
        if(tModemId == undefined || "" == tModemId){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.msg.modemNotConnected"/>');
            return false;
        }
        Ext.MessageBox.prompt('Options For Limit Power Usage', 'Please enter the value of threshold<br>',
                function(btn, numTxt) {
                    // Threshold value (textbox)
                    var powerThr = numTxt.trim();
                    if(btn=='ok'){
                        // 입력 값 검증
                        if(isNaN(powerThr)==true || powerThr.length < 1){
                            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.msg.onlyNumber'/>');
                            return false;
                        }
                        // Ajax 정의
                        Ext.define('limitPowerAjax', {
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
                        // Ajax call (timeout : 120 seconds)
                        limitPowerAjax.request({
                            url : "${ctx}/gadget/device/command/cmdLimitPowerUsage.do",
                            method : 'POST',
                            timeout : extAjaxTimeout,
                            params: {
                                mdsId : meterId,
                                loginId : loginId,
                                thresholdNormal : powerThr
                            },
                            success: function (result, request){
                                //Close the MessageBox & Take the result
                                Ext.MessageBox.hide();
                                var jsonData = Ext.util.JSON.decode( result.responseText );
                                var returnString="";
                                if(jsonData.rtnStr == undefined){
                                    returnString +=
                                            '# Operation Result : ' + jsonData.status +
                                            '\n# Message 1 : ' + jsonData.cmdResult +
                                            '\n# Detail : ' + jsonData.RESULT_VALUE;
                                }else{
                                    returnString +=
                                            '# Operation Result : ' + jsonData.status +
                                            '\n# Message 1 : ' + jsonData.rtnStr +
                                            '\n# Message 2 : ' + jsonData.cmdResult +
                                            '\n# Detail : ' + jsonData.RESULT_VALUE;
                                }
                                $('#commandResult').val(returnString);
                            },
                            failure: function(result, request){
                                //Close the MessageBox & Take the result
                                Ext.MessageBox.hide();
                                //$('#commandResult').val(result.toString());
                                if(result.isTimeout){
                                    $('#commandResult').val(" "+"Response Timeout ("+request.timeout/1000+"s)");
                                }else{
                                    $('#commandResult').val(" "+request.statusText);
                                }
                            }
                        });
                    }
                }, this, false, '1000'); //<prompt> scope=this, multiline=false, default=1000

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
    
//     function getResolutionStringByValue(innerText){ // SP-840 
//         var allInputs = document.getElementsByTagName("th");
//         var results = [];
//         for(var x=0;x<allInputs.length;x++)
//             if(allInputs[x].innerText == innerText){
//                 results.push(allInputs[x].parentNode.children[1].innerText);
//                 break;
//             }
//         return results;
//     }
    
    function onDemand() {
        if (!onDemandValidate()) {
            return;
        }
        //var resolutionStr = getResolutionStringByValue("Resolution");
        var resolution;
        try{
        	resolution = document.getElementById("esolution_container").children[0].getElementsByClassName("selected")[0].id.replace(/[^0-9]/g,''); // Regex : Not Include 0~9 , Global => Remove
        }catch(error){
        	resolution = 1;
        	console.log(error);
        }

       	// INSERT START SP-179
		var type = $('input[name=chk_info]:checked').val();
		var fromdate = "";
		var todate = "";
	    var timeouts = [];
	    var timeout = 90000; // default 90s
	    var p = null;
    	// INSERT END SP-179
        //비동기 설정
        var day = calDateRange($('#onDemandFromDateHidden').val(),$('#onDemandToDateHidden').val());
        if(type == "MCU"){
            var dcuHandshakingTimeout = parseInt( ${dcuHandshakingTimeout} )*1000;
            var dcuDayTimeout = parseInt( ${dcuDayTimeout} )*1000 ;
            timeout = dcuHandshakingTimeout + (dcuDayTimeout * (day+1) * (60/resolution));
        }else if(type == "MODEM"){
            var modemHandshakingTimeout = parseInt( ${modemHandshakingTimeout} )*1000;
            var modemDayTimeout = parseInt( ${modemDayTimeout} )*1000;
            timeout = modemHandshakingTimeout + (modemDayTimeout * (day+1) * (60/resolution));
        }else if(type == "METER"){
            var meterHandshakingTimeout = parseInt( ${meterHandshakingTimeout} )*1000;
            var meterDayTimeout = parseInt( ${meterDayTimeout} )*1000;
            timeout = meterHandshakingTimeout + (meterDayTimeout * (day+1) * (60/resolution));
        }
            
        $.ajaxSetup({
            async : true
        });

        function progressBar(v, limit) {
            return function(){
        		if(v == limit) {
        		    Ext.MessageBox.hide();
             		escape();
                  	Ext.Msg.show ({
                    	title: 'On-Demand Metering',
                      	msg: 'Timeout',
                      	minWidth:150,
                      	closable:false,
                      	buttons: Ext.Msg.OK,
                      	buttonText: {
                          	ok: 'ok'
                      	}
                 	});
               } else {
                  var i = v/limit;
                  Ext.MessageBox.updateProgress(i, (limit-v)+'sec remain.');
               }
            };
         };
        function showProgressBar(timeout) {
            var sec = timeout/1000;
            for(var i = 1; i <= sec; i++) {
        		timeouts.push(setTimeout(progressBar(i, sec), i*1000));
            }
        }
        Ext.Msg.show ({
            title: 'On-Demand Metering',
            msg: 'Waiting for response.',
            progressText: timeout/1000 + 'sec remain.',
            width:250,
            progress:true,
            closable:true,
            buttons: Ext.Msg.CANCEL,
            buttonText: {
                cancel: 'cancle'
            },
            fn:escape
       	});
        showProgressBar(timeout);

        function escape(){
            if(p != null) p.abort();	// https://stackoverflow.com/questions/14238619/getjson-timeout-handling
            for (var i = 0; i < timeouts.length; i++) {
        	    clearTimeout(timeouts[i]);
        	}
        }

        p = $.getJSON(	'${ctx}/gadget/device/command/cmdOnDemand.do',
        	{
            	'target' : meterId,
                'loginId' : loginId,
                'fromDate' : $('#onDemandFromDateHidden').val() + "000000", //onDemandFromDateHidden
                'toDate' : $('#onDemandToDateHidden').val() + "235959",
                'type' : 'METER'		// INSERT SP-179
            },function(returnData) {
        		//원래 동기방식으로 설정
        		for (var i = 0; i < timeouts.length; i++) {
    				clearTimeout(timeouts[i]);
				}
                $.ajaxSetup({
                	async : false
                });
                $('#commandResult').val(returnData.rtnStr);
                Ext.Msg.hide();
                if (returnData.rtnStr == 'Success') {
                    Ext.Msg.show ({
                        title: 'On-Demand Metering',
                        msg: 'Success',
                        minWidth:150,
                        closable:false,
                        buttons: Ext.Msg.OK,
                        buttonText: {
                            ok: 'ok'
                        }
                   	});
                    document.getElementById("detail_view").innerHTML = returnData.detail;
                    if (returnData.detail != null  && returnData.detail != "<html></html>") {
                    	if ($("#isMx2").val() == "true") {
                        	$("#phasorDiagramTbl").show();
                            var svgLink = "<img src='${ctx}/gadget/device/viewPhasorDiagram.do"
                                             + "?volAng_a=" + $("#volAng_a").val()
                                             + "&volAng_b=" + $("#volAng_b").val()
                                             + "&volAng_c=" + $("#volAng_c").val()
                                             + "&curAng_a=" + $("#curAng_a").val()
                                             + "&curAng_b=" + $("#curAng_b").val()
                                             + "&curAng_c=" + $("#curAng_c").val() + "'>";
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
                    var msg = returnData.rtnStr == null ? 'Request Fail' : returnData.rtnStr;
                    Ext.Msg.show ({
                        title: 'On-Demand Metering',
                        msg: msg,
                        minWidth:150,
                        closable:false,
                        buttons: Ext.Msg.OK,
                        buttonText: {
                            ok: 'ok'
                        }
                   	});
                }
            } // function(returnData) End.
        ); // $.getJSON End.
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


            if (rtnStr == 'Success') {
                Ext.Msg.alert('', 'Success!', null, null);
                $('#commandResult').val(rtnStr);
            } else if ( rtnStr == '' || rtnStr == null){
                Ext.Msg.alert('', 'Fail', null, null);
                $('#commandResult').val("No Result");
            } else{
                Ext.Msg.alert('', rtnStr, null, null);
                $('#commandResult').val(rtnStr); 	
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
    	/* Ext.Msg.prompt('Options For Ping', 'Please enter packet-size and echo-count.<br/><br/>Desc) packet-size, echo-count <br/>',
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
    	},this, false, '64, 3'); */
    	
    	doPing('64', '3');
    }
    
	function commandCOAPPing() {
		/* Ext.Msg.prompt('Options For COAP Ping', 'Please enter the echo-count.<br/>',
    			function(btn, text) {
    				if(btn=='ok'){
    					var count = text;
    					if(count != null) {
							// 입력된 값이 숫자가 아닐 경우 오류 처리
							if(isNaN(count) == true) {
								Ext.Msg.alert('Error', 'Please enter number.');
								return false;
							}else{
								doCOAPPing(count);
							}
    					} else {
    						Ext.Msg.alert('Error', 'Please re-enter the echo-count.');
							return false;
    					}
    				}
    	},this, false, '1'); */
		doCOAPPing();
	}

	function doPing(packetSize, count) {
		//비동기 설정
		$.ajaxSetup({
			async : true
		});
		var modemId = $('#modemId').val();
		$('#commandResult').val("Request METER Server Ping....");
		$.ajax({
    	  url: '${ctx}/gadget/device/command/cmdModemPing.do',
    	  dataType: 'json',
    	  async: true,
    	  data: {'target' : modemId
              , 'loginId' : loginId
              , 'packetSize' : packetSize
              , 'count' : count
              , 'device' : 'meter'
              },
    	  success: function(returnData) {
	   		  if(!returnData.status){
	                 $('#commandResult').val("FAIL");
	                    return;
	             }
	             if(returnData.status.length>0 && returnData.status!='SUCCESS'){
	                 $('#commandResult').val("Meter Ping: "+returnData.status);
	             } else {
	             	$('#commandResult').val(""); 
	                 $('#commandResult').val(returnData.jsonString); 
	             }
    	  }
    	});
	}
	
	function doCOAPPing() {
		Ext.Msg.wait('Waiting for response.', 'Wait !');
		var modemId = $('#modemId').val();
		$('#commandResult').val("Request METER Server COAP-Ping....");
		//비동기 설정
		$.ajaxSetup({
			async : true
		});
		
		$.getJSON('${ctx}/gadget/device/command/cmdModemCOAPPing.do', {
			'target' : modemId,
			'loginId' : loginId,
			'device' : 'meter'
		},function(returnData) {
			Ext.Msg.hide();
			$('#commandResult').val("");
			$('#commandResult').val(returnData.jsonString);
		});
	}

	function commandTraceroute() {
		Ext.Msg.prompt('Options For Traceroute', 'Please enter hop count.<br/>The maximum number of hops for the target search.<br/>',
				function (btn, text) {
					if (btn=='ok') {
						if (text != null) {
							if (3 < text.length || text.length < 1 || text == 0 || text > 30) {
								Ext.Msg.alert('Error', 'The maximum hop count is 30.<br/>Please re-enter hop count.');
								return false;
							}
							
							// 입력된 값이 숫자가 아닐 경우 오류 처리
							if (isNaN(text) == true) {
								Ext.Msg.alert('Error', 'Please enter number.');
								return false;
							} else {
								doTraceroute(text);	
							}
						} else {
							Ext.Msg.alert('Error', 'Please re-enter hop count.');
							return false;
						}							
					}
		},this, false, '5');
	}

	function doTraceroute(hopCount) {
		var modemId = $('#modemId').val();
		
		Ext.Msg.wait('Waiting for the response.', 'Message');        	
		$('#commandResult').val("Request Meter Traceroute....");
		$.ajax({
		  url: '${ctx}/gadget/device/command/cmdModemTraceroute.do',
		  dataType: 'json',
		  async: true,
		  data: {'target' : modemId
			  ,'loginId' : loginId
			  ,'hopCount' : hopCount},
		  success: function(returnData) {
			if (!returnData.status) {
				Ext.Msg.hide();
				$('#commandResult').val("FAIL");
				return;
			}
			
			if (returnData.status.length>0 && returnData.status!='SUCCESS') {
				Ext.Msg.hide();
				$('#commandResult').val("Meter Traceroute: " + returnData.status);
			} else {
				Ext.Msg.hide();
				$('#commandResult').val(""); 
				$('#commandResult').val(returnData.jsonString); 
			}
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
								Ext.Msg
										.alert('', returnData.rtnStr, null,
												null);
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
								Ext.Msg
										.alert('', returnData.rtnStr, null,
												null);
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
								Ext.Msg
										.alert('', returnData.rtnStr, null,
												null);
							}
						});
	}
	
	
	
	function commandGetCurrentLoadLimit() {
        Ext.Msg.wait('Waiting for response.', 'Wait !');
        var param = '';
        
        if(targetType == "DCU") {
            param = {
                  'target' : mcuId
                , 'loginId' : loginId
                , 'cmd' : 'cmdGetCurrentLoadLimit'
                , 'meterId': meterId
            }
        } else if(targetType == "SMS") {
            var modemId = $('#modemId').val();
            param = {
                  'loginId' : loginId
                , 'cmd' : 'cmdGetCurrentLoadLimit' 
                , 'modemId': modemId
            }
        }
            
        Ext.Ajax.request({
        url : targetUri,
        timeout: 300000,
        method : 'POST',
        params : param,
        success : function(returnData) {
            var jsonData = Ext.util.JSON.decode( returnData.responseText );
            $('#commandResult').val(jsonData.rtnStr);
            Ext.MessageBox.hide();
        },
        failure : function(result, request){
            $('#commandResult').val(result.statusText+"\n");
            Ext.MessageBox.hide();
            }
        });
    }

    function commandSetCurrentLoadLimit() {
        if(Ext.getCmp('setCurrentLoadLimitPanel')){
            Ext.getCmp('setCurrentLoadLimitPanel').close();
        } 
        
        var currentLoadLimitFormPanel =  new Ext.form.FormPanel({                                           
                id          : 'cLLformpanel',
                defaultType : 'fieldset',        
                bodyStyle:'padding:0px 0px 0px 0px',
                frame       : true,
                
                labelWidth  : 100,              
                items       : [
                   {
                        xtype: 'textfield',
                        id : 'durationJudgeTime',
                        width : 240,
                        //emptyText: 'tt:mm:ss',
                        fieldLabel: ' Duration Judge Time (min)',
                        disabled : false,
                    },
                    {
                        xtype: 'textfield',
                        id : 'threshold',
                        width : 240,
                        //emptyText: 'dd',
                        fieldLabel: ' Threshold (%)',
                        disabled : false,
                    }                       
                ],
                buttons: [
                    {
                        id : 'cllButions',
                        text: ' OK ',
                        handler: function() {
                            var param = '';
                            var durationJudgeTime = Ext.getCmp('durationJudgeTime').getValue();
                            var threshold = Ext.getCmp('threshold').getValue();
                            /* var blank_pattern = /[\s]/g;
                            // Blank 및 Space 체크
                            if( blank_pattern.test(durationJudgeTime) || blank_pattern.test(threshold) || durationJudgeTime=="" || threshold==""){
                            	Ext.Msg.alert('<fmt:message key='aimir.message'/>','Empty value is not allowed');
                                return false;
                            // 숫자인지 체크    
                            }else if(isNaN(durationJudgeTime) || isNaN(threshold)){ 
                            	Ext.Msg.alert('<fmt:message key='aimir.message'/>','Argument is only a Number');
                            	return;
                            // durationJudgeTime 값 범위 체크
                            }else if(durationJudgeTime < 0 || durationJudgeTime > 60){  
                            	Ext.Msg.alert('<fmt:message key='aimir.message'/>','Argument(Duration Judge Time) is not valid value </br></br> <center><font color="red">0 ≤ Duration Judge Time(s) ≤ 60</font></center>');
                            	return;
                            // threshold 값 범위 체크
                            }else if(threshold < 0 || threshold > 100){ 
                            	Ext.Msg.alert('<fmt:message key='aimir.message'/>','Argument(Threshold) is not valid value </br></br> <center><font color="red">0 ≤ Threshold(%) ≤ 100</font></center>');
                            	return;
                            } */
                            
                            if(targetType == "DCU") {
                                param = {
                                      'target' : mcuId
                                    , 'loginId' : loginId
                                    , 'cmd' : 'cmdSetCurrentLoadLimit'
                                    , 'meterId': meterId
                                    , 'judgeTime' : durationJudgeTime
                                    , 'threshold' : threshold
                                }
                            } else if(targetType == "SMS") {
                                var modemId = $('#modemId').val();
                                param = {
                                      'cmd' : 'cmdSetCurrentLoadLimit' + '/' + durationJudgeTime + '/' + threshold
                                    , 'loginId' : loginId
                                    , 'modemId' : modemId
                                }
                            }
                            
                            Ext.Msg.wait('Waiting for response.', 'Wait !');                            
                            Ext.Ajax.request({
                                url : targetUri,
                                timeout: 300000,
                                method : 'POST',
                                params : param,
                                success : function(returnData) {
                                    var jsonData = Ext.util.JSON.decode( returnData.responseText );
                                    $('#commandResult').val(jsonData.rtnStr);
                                    Ext.MessageBox.hide();
                                    Ext.getCmp('setCurrentLoadLimitPanel').close();
                                },
                                failure : function(result, request){
                                    $('#commandResult').val(result.statusText+"\n");
                                    Ext.MessageBox.hide();
                                    Ext.getCmp('setCurrentLoadLimitPanel').close();
                                }
                            });
                        }
                        
                    },{
                        text: 'Cancel',
                        listeners: {
                            click: function(btn,e) {
                                Ext.getCmp('setCurrentLoadLimitPanel').close();
                            }
                        }
                }]
            });
        
            var currentWindow = new Ext.Window({
                id     : 'setCurrentLoadLimitPanel',
                title  : ' Current Load Limit ',
                pageX : 600,
                pageY : 500,
                height : 180,
                width  : 290,
                layout : 'fit',
                bodyStyle   : 'padding: 5px 5px 5px 5px;',
                items  : [currentLoadLimitFormPanel],
            });
        
            currentWindow.show();
    }
    

	$(function() {
		if (permitLocationId != null && permitLocationId != "") {
			locationTreeForPermitLocation('treeDivA', 'searchWord_1',
					'sLocationId', permitLocationId);
		} else {
			locationTreeGoGo('treeDivA', 'searchWord_1', 'sLocationId');
		}
	});

	/************ Fusion Chart **************/ //ibk all
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

		/* fcLogChart = new FusionCharts(
				"${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "fcLogChartId",
				colWidth, "250", "0", "0");*/
		 
		fcLogChart = new FusionCharts({
	        type: 'column3d',
	        width: colWidth,
	        height: '255'});
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

		/* fcMeasureChart = new FusionCharts(
				"${ctx}/flexapp/swf/fcChart/MSColumn3D.swf",
				"fcMeasureChartId", colWidth, "250", "0", "0"); */
		console.log("showLogChart()");
		console.log(fcMeasureChartDataXml);
		fcMeasureChart = new FusionCharts({
	        type: 'column3d',
	        width: colWidth,
	        height: '250'});
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
		$.getJSON(
						'${ctx}/gadget/device/getMeterSearchChart.do',
						{
							sMeterType : condition[0],
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
							sMeterAddress : condition[21],
							sGs1 : condition[22],
							sMbusSMYN : condition[23],
							sDeviceSerial	   : condition[24]
							
						},
						function(json) {
							var list = json.chartData;
							fcPieChartDataXml = "<chart "
                                      + "showPercentValues='1' "
                                      + "showPercentInToolTip='0' "
                                      + "showLabels='0' "
                                      + "showValues='1' "
                                      + "showLegend='1' "
                                      + "legendPosition='Right' "
                                      + "manageLabelOverflow='1' "
                                      + "useEllipsesWhenOverflow='0' "
                                      + ">";
							var labels = "";

							var emptyFlag = true;
							for (index in list) {
								if (list[index].label == "fmtMessage") {
									labels += "<set label='activity within &lt;br /&gt; 24 hours' value='" + list[index].data + "' color='"+blue+"' />";
									if (list[index].data > 0)
										emptyFlag = false;
								} else if (list[index].label == "fmtMessage24") {
									labels += "<set label='<fmt:message key='aimir.commstateYellow' />' value='"
											+ list[index].data
											+ "' color='"
											+ green + "' />";
									if (list[index].data > 0)
										emptyFlag = false;
								} else if (list[index].label == "fmtMessage48") {
									labels += "<set label='<fmt:message key='aimir.commstateRed' />' value='"
											+ list[index].data
											+ "' color='"
											+ yellow + "' />";
									if (list[index].data > 0)
										emptyFlag = false;
								} else if (list[index].label == "fmtMessage99") {
									labels += "<set label='<fmt:message key='aimir.bems.facilityMgmt.unknown' />' value='"
											+ list[index].data
											+ "' color='"
											+ redbean + "' />";
									if (list[index].data > 0)
										emptyFlag = false;
								} else if (list[index].label == "CommError") {
									labels += "<set label='CommError' value='"
										+ list[index].data
										+ "' color='"
										+ orange + "' />";
								if (list[index].data > 0)
									emptyFlag = false;
								} else if (list[index].label == "SecurityError") {
								labels += "<set label='SecurityError' value='"
									+ list[index].data
									+ "' color='"
									+ red + "' />";
								if (list[index].data > 0)
								emptyFlag = false;
								} else if (list[index].label == "PowerDown") {
									labels += "<set label='PowerDown' value='"
										+ list[index].data
										+ "' color='"
										+ gray + "' />";
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
	                              + ">";
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
	                //+ "showPercentValues ='1'"
	                + "legendPosition='RIGHT' "
	                + "PYAxisName='<fmt:message key="aimir.usage"/>' "
	                + ">"
	                + "<categories><category label=' ' /></categories>"
	                + "<set value='0' label='<fmt:message key='aimir.usage'/>[kWh]' />"
	                + "<set value='0' label ='<fmt:message key='aimir.co2formula2'/>[kg]' color = '" + green + "'/>"  
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
	                              + "labelDisplay = 'WRAP' "
	                              //+ "labelStep='" + (list.length / 8) + "' "  // SP-852
	                               + ">";
	                      var categories = "<categories>";
	                      var dataset1 ="";
	                      var dataset2 ="";
	                              + "blue" + "' >";

	                      for (index in list) {
	                          if (list[index].meteringDate != null) {
	                              categories += "<category label='"+list[index].meteringDate +"' />";

	                              dataset1 += "<set value='"+list[index].usage+"' label='<fmt:message key='aimir.usage'/>'/>";
	                              dataset2 += "<set value='"+list[index].co2+"' color='"+green+"' label='<fmt:message key='aimir.co2formula2' />[kg]' />";
	                          }
	                      }

	                      if (list == null || list.length == 0) {
	                          categories += "<category label=' ' />";

	                          dataset1 += "<set value='0' label='<fmt:message key='aimir.usage'/>[kWh]' />"
	                          dataset2 += "<set value='0' label='<fmt:message key='aimir.co2formula2'/>[kg]' color='" + green + "'/>" 
	                      }

	                      categories += "</categories>";

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
		fcPieChart = new FusionCharts({
	        type: 'pie3d',
	        width: $('#fcPieChartDiv').width(),
	        height: '181'});
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
		console.log("fcChartRender()");
		console.log(fcMeasureChartDataXml);
		fcMeasureChart = new FusionCharts({
	        type: 'column3d',
	        width: colWidth,
	        height: '250',
	        });
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

			for (var i = 0; i < len; i++) {
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
		fmtMessage[17] = "<fmt:message key="aimir.install"/> "
				+ "<fmt:message key="aimir.id"/>";
		fmtMessage[18] = "<fmt:message key="aimir.energymeter.ct"/>";
		fmtMessage[19] = "<fmt:message key="aimir.installProperty"/>";
		fmtMessage[20] = "<fmt:message key="aimir.customeraddress"/>";
		fmtMessage[21] = "<fmt:message key="aimir.meter.transformerRatio"/>";

		fmtMessage[22] = "<fmt:message key="aimir.address1"/>";
		fmtMessage[23] = "<fmt:message key="aimir.address2"/>";
		fmtMessage[24] = "<fmt:message key="aimir.address3"/>";
		fmtMessage[25] = "<fmt:message key="aimir.meter.address"/>";
		fmtMessage[26] = "<fmt:message key="aimir.sw.hw.ver"/>";
        fmtMessage[27] = "<fmt:message key="aimir.modemid"/>";
        fmtMessage[28] = "<fmt:message key="aimir.shipment.gs1"/>";


		obj.condition = getCondition();
		obj.fmtMessage = fmtMessage;

		if (win)
			win.close();

		win = window.open("${ctx}/gadget/device/meterMaxExcelDownloadPopup.do",
				"meterExcel", opts);
		win.opener.obj = obj;
	}

	var winPopup;
    function openCommInfoExcelReport() {
    	var opts = "width=600px, height=400px, left=100px, top=100px,  resizable=no, status=no";
        var obj = new Object();
        var fmtMessage = new Array();
        
        fmtMessage[0] = "Communication Info.";
        fmtMessage[1] = "<fmt:message key="aimir.number"/>";
        fmtMessage[2] = "<fmt:message key="aimir.mcuid"/>";
        fmtMessage[3] = "<fmt:message key="aimir.24within"/>";
        fmtMessage[4] = "<fmt:message key="aimir.24over"/>";
        fmtMessage[5] = "<fmt:message key="aimir.48over"/>";
        fmtMessage[6] = "<fmt:message key="aimir.bems.facilityMgmt.unknown"/>";
        fmtMessage[7] = "CommError";
        fmtMessage[8] = "SecurityError";
        fmtMessage[9] = "PowerDown";
    	
        obj.condition   = getCondition();
        obj.fmtMessage  = fmtMessage;

        if(winPopup) {
        	winPopup.close();
        }
        
        winPopup = window.open("${ctx}/gadget/device/meterCommInfoExcelDownloadPopup.do", "MeterListExcel", opts);
        winPopup.opener.obj = obj;
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
		$.getJSON("${ctx}/common/getDate.do", {
			searchDate : bfDate,
			addVal : val,
			supplierId : supplierId
		}, function(json) {
			var dateId = '#' + fieldname;
			var dateHiddenId = '#' + fieldname + 'Hidden';

			$(dateId).val(json.searchDate);
			$(dateHiddenId).val(json.searchDate);

			$.getJSON("${ctx}/common/convertSearchDate.do", {
				searchStartDate : $(dateHiddenId).val(),
				searchEndDate : $(dateHiddenId).val(),
				supplierId : supplierId
			}, function(json) {
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
		if (modemType == "SubGiga" && modemProtocolType == "IP") {	// RF
			$("#selectDateForDcu").show();
		} else { // Eth, MBB
			$("#selectDateForDcu").hide();
		}
		
		$("#selectDate").show();
		initDateCondition();
	}

	// FUNCTION UI TEST : SAMPLE
	var chk = 0;
	function successFail(packetSize, count) {
		if (chk == 0) {
			$('#commandResult').val("Success");
			chk++;
		} else if (chk == 1) {
			$('#commandResult').val("Fail");
			chk--;
		}
	}
	
	//ibk OTA 
	var uploadWindow;
	var uploadFormPanel;
	function drawUploadPanel() {
		// 아직 안닫힌 경우 기존 창은 닫기
		if (Ext.getCmp('uploadWindowPanel')) {
			Ext.getCmp('uploadWindowPanel').close();
		}

		var uploadFormPanel = new Ext.form.FormPanel({
			id : 'formpanel',
			defaultType : 'fieldset',
			bodyStyle : 'padding:0px 0px 0px 0px',
			frame : true,

			labelWidth : 100,
			items : [
			/* {
			 	xtype: 'textfield',
			 	id : 'id',
			 	width : 240,
			 	emptyText: 'test',
			     fieldLabel: ' take over ',
			     disabled : true,
			 }, */{
				xtype : 'label',
				id : 'infolabel',
				text : ' Are you sure to upload OTA File ?',
			}

			],
			buttons : [ {
				id : 'ota',
				text : ' OK ',
			// click => AjaxUpload

			}, {
				text : 'Cancel',
				listeners : {
					click : function(btn, e) {
						Ext.getCmp('uploadWindowPanel').close();
					}
				}
			} ]
		});

		var uploadWindow = new Ext.Window({
			id : 'uploadWindowPanel',
			title : ' OTA ',
            modal: true,
            pageX : 600,
            pageY : 500,
			height : 120,
			width : 290,
			layout : 'fit',
			bodyStyle : 'padding: 5px 5px 5px 5px;',
			items : [ uploadFormPanel ],
		});

		uploadWindow.show();

		new AjaxUpload(
				'ota',
				{
					name : 'otaFile',
					responseType : 'json',
					onSubmit : function(file, ext) {
						//파일 확장자 검색
						if (!(ext && /^(dwl|DWL|bin|BIN|mot|MOT)$/.test(ext))) {
							Ext.Msg.alert('<fmt:message key='aimir.message'/>', 'is not OTA file');
							return false;
						}                        
/*
                        if(targetType == "DCU") {
                            this._settings.action = '${ctx}/gadget/device/command/cmdReqNodeUpgrade.do',
                            this._settings.data = {
                                meterId     : meterId,
                                loginId     : loginId,
                                ext         : ext,
                                target      : mcuId,
                                reqType     : "meter"    
                            };  
                        } else if(targetType == "SMS") {
                            var modemId = $('#modemId').val();
                            this._settings.action = targetUri,
                            this._settings.data = {
                                    modemId     : modemId,
                                    loginId     : loginId,
                                    cmd         : 'cmdMeterOTAStart',
                                    ext         : ext
                            };
                        }      
*/

                        this._settings.action = '${ctx}/gadget/device/command/cmdOTAStart.do',
                        this._settings.data = {
                            targetType  : "METER",                            
                            deviceId     : meterId,
                            loginId     : loginId,
                            ext         : ext,
                            target      : mcuId,

                        };  

						Ext.Msg.wait('Waiting for response.', 'Wait !');
						return true;
					},
					onComplete : function(file, response) {
						Ext.Msg.hide();
                        uploadWindow.close();
						Ext.Msg.alert('OTA', response.rtnStr);
					}
		});
	}

	function popupOBIS() {
		var cmdLineWin;
		var left = ($("#fcPieChartDiv").width()-200);
		var opts = "width=1000px, height=500px, left=" + left
				+ "px, top=200px, resizable=no, status=no, location=no";

		var obj = new Object();
		var condition = new Array();
		obj.modelId = $('#modelId').val();
		obj.loginId = loginId;
		obj.meterId = meterId;
		obj.mdsId = $('#mdsId').val();
		obj.modelName = $('#modelIdView').val()
		obj.supplierId = supplierId ;
		if (cmdLineWin) {
			cmdLineWin.close();
		}
		
		if ( meterId == "" || meterId == undefined ){
            Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key='aimir.selectmeter'/>');
            return false;
		}
		cmdLineWin = window.open("${ctx}/gadget/device/meterOBIS.jsp"
				, "OBIS",
				opts);
		cmdLineWin.opener.obj = obj;
	}

    function commandGetMeterFWVersion() {
        // RF모뎀인경우 NullBypass로 보낼지 DCU를 통해 보낼지 선택하도록 함.
        var usingNullBypass = false;
        if(modemType == "SubGiga" && modemProtocolType == "IP"){
/*            
            var checkWindow = new Ext.Window({
                id : 'checkUsingNullBypass',
                title : 'Information',
                modal: true,
                pageX : 600,
                pageY : 500,
                height : 150,
                width : 290,
                layout : 'fit',
                bodyStyle : 'padding: 5px 5px 5px 5px;',
                items : [{
                    xtype: 'form',
                    layout: 'form',
                    defaultType : 'fieldset',
                    bodyStyle : 'padding:0px 0px 0px 0px',
                    frame : true,
                    labelWidth : 100,
                    items : [{
                        xtype: 'label',
                        id : 'infolabel',
                        text : 'If you want Using Bypass, Please check.'
                    }, {
                          id: 'usingNullBypass'
                        , xtype: 'checkbox'
                        , boxLabel: 'Using Bypass'
                        , checked: true
                        , inputValue : 1
                    }]
                }]
                , buttons : [{
                      text : ' OK ',
                      listeners : {
                           click : function(btn, e) {
                                usingNullBypass = Ext.getCmp('usingNullBypass').getValue();
                                sendReqCommandGetMeterFWVersion(usingNullBypass);
                                Ext.getCmp('checkUsingNullBypass').close();
                           }
                      }
                }, {
                    text : 'Cancel',
                    listeners : {
                        click : function(btn, e) {
                            usingNullBypass = 0;
                            Ext.getCmp('checkUsingNullBypass').close();
                        }
                    }
                }]
            });
            checkWindow.show();
*/
          sendReqCommandGetMeterFWVersion(true);            
        }else {
            sendReqCommandGetMeterFWVersion(usingNullBypass);
        }    

    } 

    function sendReqCommandGetMeterFWVersion(usingNullBypass) {
        Ext.Msg.wait('Waiting for response.', 'Wait !');

        Ext.Ajax.request({
        url : '${ctx}/gadget/device/command/cmdGetMeterFWVersion.do',
        method : 'POST',
        timeout : extAjaxTimeout + 120000,
        params : {
            targetType  : "METER",
            deviceId    : meterId,
            loginId     : loginId,
            isNullBypass : usingNullBypass
        },
        success : function(returnData) {
            var jsonData = Ext.util.JSON.decode( returnData.responseText );
            $('#commandResult').val(jsonData.rtnStr);
            Ext.MessageBox.hide();
        },
        failure : function(result, request){
            $('#commandResult').val(result.statusText+"\n");
            Ext.MessageBox.hide();
            }
        });
    }
    
    var otaWin;
    function runOta(){
		var opts = "width=800px, height=550px, left=" + 200 + "px, top=200px, resizable=no, status=no, location=no";
		var obj = new Object();
		obj.pageWidth = '800';
		obj.pageHeight = '550';
		obj.deviceModel = "";
		obj.modelName = tempModelName;
		obj.condition = "";
		obj.equip_kind = "meter";
		obj.deviceIdString = tempDeviceId;
		obj.loginId = loginId;
		obj.locationId = tempLocId;
		obj.targetDeviceType = "";
		if (otaWin){
			otaWin.close();
		}
			
		otaWin = window.open("${ctx}/gadget/device/firmware/firmwareAddPopup.do", 
								"firmwareAdd", opts);
		otaWin.opener.obj = obj;			
	}
    
	// Async History    
    var asyncHistoryGridStore;
    var asyncHistoryGridModel;
    var asyncHistoryGridPanel;
    var asyncHistoryGridInstanceOn = false;
    function getAsyncHistoryGrid(){
    	
    	checkMeterId();
        //setting grid panel width
        var gridWidth = $("#gadget_body").width()-20;
        var pageSize = 5;
        asyncHistoryGridStore = new Ext.data.JsonStore({
        	 autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/getAsyncLogListForMeter.do",
            //파라매터 설정.
            baseParams: {
            	meterId: meterId,
            	startDate : $("#asyncCommandHistoryStartDateHidden").val(),
                endDate : $("#asyncCommandHistoryEndDateHidden").val(),
                loginId : loginId
            },
            totalProperty: 'totalCount',
            root:'rtnStr',
            fields: [
					   "rowNo"
                     , "command"
                     , "requestTime"
                     , "state"
                     , "deviceSerial"
                     , "trid"
            ],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                         });
                }
            }
            
        });//Store End

        asyncHistoryGridModel = new Ext.grid.ColumnModel({
            columns: [
				 {header: "No.", dataIndex: 'rowNo', width: 100, align: 'center'}
				,{header: "Transaction ID", dataIndex: 'trid', width:gridWidth/6, align: 'center'}
				,{header: "Request Time", dataIndex: 'requestTime', width:gridWidth/6, align: 'center'}
                ,{header: "Command", dataIndex: 'command', width:gridWidth/6, align: 'center'}
                ,{header: "Modem Serial", dataIndex: 'deviceSerial', width:gridWidth/6, align: 'center'}
                ,{header: "State", dataIndex: 'state', width:gridWidth/6, align: 'center',
                	 renderer : function(value, me, record, rowNumber, columnIndex, store) {
         		 		if(record.data.state == "0") //Security Error
             				return "Success";
         		 		else if(record.data.state == "1")
         		 			return "Waiting";
         		 		else if(record.data.state == "2")
         		 			return "Running";
         		 		else if(record.data.state == "4")
         		 			return "Terminate";
         		 		else if(record.data.state == "8")
         		 			return "Delete";
         		 		else if(record.data.state == "255")
         		 			return "Unknown";
         		 		else
         		 			return record.data.state
             			
            				}	
                }
                ,{header: "Result", align: 'center', width:gridWidth/6-105,
                    renderer: function(value, metaData, record, index) {
                    	var data = record.data;
                    	asyncRowNo = data.rowNo;
                    	asyncDeviceId = data.deviceSerial;
                        asyncTrid = data.trid;
                        asyncState = data.state;
                        asyncCommand = data.command;
                        var btnHtml = "<a href='#;' onclick='asyncResultCheck();' class='btn_blue'><span>Result</span></a>";
                        var tplBtn = new Ext.Template(btnHtml);
                        return tplBtn.apply();
                    }
                }

            ],
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 120
            }
        });

        if (asyncHistoryGridInstanceOn == false) {
            //Grid panel instance create
            asyncHistoryGridPanel = new Ext.grid.GridPanel({
                store: asyncHistoryGridStore,
                colModel : asyncHistoryGridModel,
               //selectModel define.
                singleSelect:true,
                sm : new Ext.grid.RowSelectionModel({
        			singleSelect:true,
        			listeners: {
                        rowselect: function(smd, row, rec) {
                        	var data = rec.data;
                        	asyncRowNo = data.rowNo;
                        	asyncDeviceId = data.deviceSerial;
                            asyncTrid = data.trid;
                            asyncState = data.state;
                            asyncCommand = data.command;
                        }
                    }
        		}),
                autoScroll:true,
                height : 190,
                //scroll:false,
                width:  gridWidth,
                style: 'align:center;',
                //패널 높이 설정
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                //랜더링 디비전
                renderTo: 'asyncHistoryGrid',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                },
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    store: asyncHistoryGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            asyncHistoryGridInstanceOn = true;
        } else {
            asyncHistoryGridPanel.setWidth( gridWidth);
            var bottomToolbar = asyncHistoryGridPanel.getBottomToolbar();
            asyncHistoryGridPanel.reconfigure(asyncHistoryGridStore, asyncHistoryGridModel);
            bottomToolbar.bindStore(asyncHistoryGridStore);
        }
    }// End of getMeterListByNotModemGrid
    
    var asyncWin;
    function asyncResultCheck(){
    	if(asyncState != 0)
    		Ext.Msg.alert('<fmt:message key='aimir.message'/>','No Result');
    	else{
    		$.getJSON('${ctx}/gadget/device/getAsyncResultForMeter.do'
    	            , { 'deviceSerial' : asyncDeviceId,
    	            	'trid' : asyncTrid,
    	            	'command' : asyncCommand
    				}
    	            , function (returnData){
    	            	if(returnData.result=="")
    	            		Ext.Msg.alert('<fmt:message key='aimir.message'/>','No Result');
    	            	else{
    	            		var opts = "width=450px, height=340px, left=" + 650 + "px, top=200px, resizable=no, status=no, location=no";
    	            		var obj = new Object();
    	            		obj.result = returnData.result;
    	            		if (asyncWin){
    	            			asyncWin.close();
    	    	    		}
    	            		asyncWin = window.open("${ctx}/gadget/device/asyncResultPopup.do", 
    	    	    								"firmwareAdd", opts);
    	            		asyncWin.opener.obj = obj;	
    	            		//Ext.Msg.alert('RESULT',returnData.result);
    	            	}
    	            });
    	}
    }
    
    // Collect Meter Values by Real Time (S)
    var collectMeterValuesGridStore;
    var collectMeterValuesGridModel;
    var collectMeterValuesGridPanel;
    var collectMeterValuesGridInstanceOn = false;
    function getCollectMeterValuesGrid() {
    	
    	checkMeterId();
    	
        var gridWidth = $("#gadget_body").width()-20;
        var gridHeight = 150;
        var pageSize = 5;
        
        collectMeterValuesGridStore = new Ext.data.JsonStore({
        	autoLoad: {params:{start: 0, limit: pageSize}},
            url: "${ctx}/gadget/device/getRealTimeMeterValues.do",
            baseParams: {
            	supplierId : supplierId,
            	deviceType : 'Meter',
            	meterId    : meterId,
            	meterMds   : meterMds,
            	searchStartDate  : $("#collectMeterValuesStartDateHidden").val(),
            },
            totalProperty: 'totalCount',
            root:'result',
            fields: [
					   "num",
					   "meteringTime",
					   "ch1",
					   "ch2",
					   "ch3",
					   "ch4",
					   "ch5",
					   "ch6",
					   "ch7"
            ],
            listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                  		page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
					});
                }
            }
            
        });//Store End

        collectMeterValuesGridModel = new Ext.grid.ColumnModel({
            columns: [
				  {header: "<fmt:message key='aimir.number'/>", dataIndex: 'num', width: 40, align: 'center'}
				, {header: "<fmt:message key='aimir.meteringtime'/>", dataIndex: 'meteringTime', width: gridWidth/8+35, align: 'center'}
				, {header: "<fmt:message key='aimir.comm.active.import'/>", dataIndex: 'ch1', width: gridWidth/8, align: 'center'}
				, {header: "<fmt:message key='aimir.comm.active.export'/>", dataIndex: 'ch2', width: gridWidth/8, align: 'center'}
				, {header: "<fmt:message key='aimir.comm.reactive.import'/>", dataIndex: 'ch3', width: gridWidth/8, align: 'center'}
				, {header: "<fmt:message key='aimir.comm.reactive.export'/>", dataIndex: 'ch4', width: gridWidth/8, align: 'center'}
				, {header: "<fmt:message key='aimir.powerQuality.voltage.A'/>", dataIndex: 'ch5', width: gridWidth/9, align: 'center'}
				, {header: "<fmt:message key='aimir.powerQuality.voltage.B'/>", dataIndex: 'ch6', width: gridWidth/9, align: 'center'}
				, {header: "<fmt:message key='aimir.powerQuality.voltage.C'/>", dataIndex: 'ch7', width: gridWidth/9, align: 'center'}
            ],
            
            defaults: {
                sortable: true
               ,menuDisabled: true
               ,width: 120
            }
        });

        if (collectMeterValuesGridInstanceOn == false) {
            collectMeterValuesGridPanel = new Ext.grid.GridPanel({
                store: collectMeterValuesGridStore,
                colModel : collectMeterValuesGridModel,
                singleSelect:true,
                sm : new Ext.grid.RowSelectionModel({
        			singleSelect:true,
        			listeners: {
                    }
        		}),
                autoScroll:true,
                height : gridHeight,
                //scroll:false,
                width:  gridWidth,
                style: 'align:center;',
                stripeRows : true,
                columnLines: true,
                loadMask:{
                    msg: 'loading...'
                },
                renderTo: 'collectMeterValuesGrid',
                viewConfig: {
                    enableRowBody:true,
                    showPreview:true,
                    emptyText: '<fmt:message key="aimir.extjs.empty"/>'
                },
                bbar: new Ext.PagingToolbar({
                    pageSize: pageSize,
                    store: collectMeterValuesGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'
                })
            });
            collectMeterValuesGridInstanceOn = true;
        } else {
        	collectMeterValuesGridPanel.setWidth(gridWidth);
        	collectMeterValuesGridPanel.setHeight(gridHeight + 30);
            collectMeterValuesGridPanel.reconfigure(collectMeterValuesGridStore, collectMeterValuesGridModel);
            
            var bottomToolbar = collectMeterValuesGridPanel.getBottomToolbar();
            bottomToolbar.bindStore(collectMeterValuesGridStore);
        }
    }
	// Collect Meter Values by Real Time (E) 

    function collectMeterValues() {
    	var duration = $('#duration').val();
    	var interval = $('#interval').val();
    	if((interval == null || interval < 60) || (interval > duration*60)){
    		Ext.Msg.alert("Message", "Interval must be greater than 60(sec) and less than Collection Period(sec) ");
    		return;
    	}
    	
    	Ext.define('realTimeAjax', {
            extend: 'Ext.data.Connection',
            singleton: true,
            constructor : function(config){
                this.callParent([config]);
                this.on("beforerequest", function(){
                    Ext.MessageBox.wait("Real Time Metering", '<fmt:message key='aimir.info'/>', {
                        text: 'WAIT',
                        scope: this,
                    });
                });
                this.on("requestcomplete", function(){
                    Ext.MessageBox.hide();
                });
            }
        });
        // Ajax Call (timeout : 180 seconds -> important)
        realTimeAjax.request({
            url :  '${ctx}/gadget/device/command/cmdRealTimeMetering.do',
            method : 'POST',
            timeout : extAjaxTimeout,
            params : {
                mdsId         : meterId,
                loginId       : loginId,
                interval      : interval,
                duration      : duration
            },
            success: function (result, request){
                Ext.MessageBox.hide();
                console.log("data comming");
                var jsonData = Ext.util.JSON.decode( result.responseText );
                var desc = "";
                if(jsonData.result == "3"){
                	desc = " - Meter key not received yet";
                }else if(jsonData.result == "4"){
                	desc = " - Real Time Metering already in process";
                }
                var returnString = " # Operation Result : " + jsonData.status + desc +
                        "</br> # Message 1 : " + jsonData.rtnStr +
                        "</br> # Message 2 : " + jsonData.cmdResult;
                
                Ext.Msg.alert("Result", returnString);
                
                if (jsonData.status == "SUCCESS") {
                	getRealtimeMeterValues(duration);
                }
            },
            failure: function(result, request){
                Ext.MessageBox.hide();
                if(result.isTimeout){
                	Ext.Msg.alert("Time Out", " "+"Response Timeout ("+request.timeout/1000+"s)");
                }else{
                	Ext.Msg.alert("Result", request.statusText);
                }
            }
        });
    		
    }
	
    function getNetstationMonitoring() {
        var params = {
                "meterId" : meterId,
                "supplierId" : supplierId,
                "loginId" : loginId
                
            };
        $("#nsCurrentInfoDiv").load("${ctx}/gadget/device/getMbusSlaveIOInfo.do", params);
    }
    
    
    function cmdGetTariff() {

    	var mcuId = $('#mcuSysId').val();
       
/*     	//Group Command
    	//if(isCheckedElementExist()){
    		
    		    	meterArray = getCheckedElements();
    	    	    //그리드, 윈도우 높이 구하기
    	            this.gridH = 30 + Number(meterArray.length * 30);
    	            this.winH = 200 + Number(meterArray.length * 30);
    	            if (this.gridH > 600)	this.gridH = 600;
    	            if (this.winH > 700)	this.winH = 700;

    	            //그리드 데이터 생성
    	            if (meterArray != null) {
    	            	
    	                var urlArray = new Array;
    	                var paramArray = new Array;
    	                
    	                count = meterArray.length;
    	                for (var i = 0; i < count; i++) {
    	                    var gridData = meterArray[i][1];
    	                    var arrayData = [gridData, 'Wait'];
    	                    array[i] = arrayData;
    	                }
    	                
//    	        		urlArray = getUrlFromMeterArray(meterArray);
    	        		for(var i = 0; i < meterArray.length; i++){
							urlArray.push('${ctx}/gadget/device/command/cmdGetTariff.do');
						}
    	                paramArray = getParamFromMeterArray(meterArray,'cmdGetTariff');
    	        		for(var i = 0; i < paramArray.length; i++){
    	        				paramArray[i].target = paramArray[i].meterId;
    	        		}
    	        		
    	                makeGroupCommandWindow("Get Tariff");
    	                setTimeout(function(){
    	                	groupCommandTask(paramArray, urlArray, 'cmdTariff', 'POST');
    	                },100);
    	            } 
    		return;
        //}// end of Group Command */
    	
        //비동기 설정
        $.ajaxSetup({
            async : true,
            timeout: 3000
        });

        $('#commandResult').val("");
        Ext.Msg.wait('Waiting for response.', 'Wait !');

        $.getJSON('${ctx}/gadget/device/command/cmdGetTariffForPKS.do', {
            'target' : meterId,
            'mcuId' : mcuId,
            'cmd' : 'cmdGetTariff',
            'loginId' : loginId
        }, function(returnData) {
    		//원래 동기방식으로 설정
    		$.ajaxSetup({
    			async : false
    		});
    		$('#commandResult').val(returnData.rtnStr);
    		Ext.Msg.hide();
    		if (returnData.rtnStr == 'SUCCESS') {
    			//Ext.Msg.alert('', 'Success!', null, null);
    			document.getElementById("detail_view_tariff").innerHTML = returnData.detail;
    			if (returnData.detail != null && returnData.detail != "<html></html>") {
  	                 $('#detail_dialog_tariff').dialog('open');
                }
            } 
          });
    }
    function cmdSetTariffForPKS() {
    	 Ext.MessageBox
         .show({
             title : 'Tariff Get/Set',
             buttons : Ext.MessageBox.OK,
             msg : 'New feature will be added.',
             icon : Ext.MessageBox.INFO
         });
    }
    
    function cmdSetTariff() {
    	  var tariffType = '';
    	  
          var searchWin = new Ext.Window({
            title: '<b>Tariff Setting</b>',
            modal: true, closable:true, resizable: false,
            width:300, height:125,
            border:true, plain:false,                      
            items:[{
                xtype: 'panel',
                frame: false, border: false,
                items:{
                  id: 'reTypeAmount_form',
                  xtype: 'form',
                  bodyStyle:'padding:10px',
                  labelWidth: 100,
                  frame: false, border: false,
                  items: [{
                    xtype: 'label', html:'<div style="text-align:left;">' + 'Please select tariff Information.' +'</div>',  anchor: '100%'
                  },{
                      xtype: 'combo',
                      id:'tariffType_id', name: 'tariffType_name', value:'Select...',          
                      fieldLabel: 'Tariff Type', triggerAction: 'all', editable: false, mode: 'local',
                      store: new Ext.data.JsonStore({
                        autoLoad   : true,
                        baseParams: {supplierId : supplierId},
                        url: '${ctx}/gadget/device/command/getTariffType.do',
                        storeId: 'tariffTypeListStore',
                        root: 'result',
                        idProperty: 'name',
                        fields: ['name',{name: 'id', type: 'int'}],
                        listeners: {
                          load: function(store, records, options){
                            Ext.getCmp('tariffType_id').setValue(records[0].data.name);
                            tariffType = records[0].data.id;
                          }
                        }
                      }),
                      valueField: 'id', displayField: 'name',
                      anchor: '100%',
                      listeners: {
                        render: function() {
                          this.store.load();
                        },
                        select : function(combo, record, index){
                          Ext.getCmp('tariffType_id').setValue(record.data.name);
                          tariffType = record.data.id;
                        }
                      }
                  }/* , {
                    xtype: 'datefield', fieldLabel: 'Tariff Date', id: 'tariffDate_id', name: 'tariffDate_name', anchor: '100%'
                  },{
                	  xtype: 'textfield', fieldLabel: '', id: 'condLimit1_id', name: 'condLimit1_name', anchor: '100%'  
                  },{
                	  xtype: 'textfield', fieldLabel: '', id: 'condLimit2_id', name: 'condLimit2_name', anchor: '100%'  
                  } */]
                }
            }],
            
            buttons: [{
              text: 'Ok',
              handler: function() {
  				  var flag = true;
  				  if(flag && Ext.getCmp('tariffType_id').getValue() == null) {
            		  Ext.Msg.alert("","Please select Tariff");
            		  flag = false;
            		  return flag;
            	  }
            	  
            	  if(flag) {
            		
/*             		//Group Command
                	//if(isCheckedElementExist()){ 5558~5603
                		    
                		    	searchWin.close();
                		    	meterArray = getCheckedElements();
                	    	    //그리드, 윈도우 높이 구하기
                	            this.gridH = 30 + Number(meterArray.length * 30);
                	            this.winH = 200 + Number(meterArray.length * 30);
                	            if (this.gridH > 600)	this.gridH = 600;
                	            if (this.winH > 700)	this.winH = 700;

                	            //그리드 데이터 생성
                	            if (meterArray != null) {
                	            	
                	                var urlArray = new Array;
                	                var paramArray = new Array;
                	                
                	                count = meterArray.length;
                	                for (var i = 0; i < count; i++) {
                	                    var gridData = meterArray[i][1];
                	                    var arrayData = [gridData, 'Wait'];
                	                    array[i] = arrayData;
                	                }
                	                
//                	        		urlArray = getUrlFromMeterArray(meterArray);
                	        		for(var i = 0; i < meterArray.length; i++){
            							urlArray.push('${ctx}/gadget/device/command/cmdSetTariffForCEB.do');
            						}
                	                paramArray = getParamFromMeterArray(meterArray,'cmdSetTariff');
                	        		for(var i = 0; i < paramArray.length; i++){
                	        				paramArray[i].target = paramArray[i].meterId;
                	        				paramArray[i].tariffTypeCode = tariffType;
                	        		}
                	        		
                	                makeGroupCommandWindow("Set Tariff");
                	                setTimeout(function(){
                	                	groupCommandTask(paramArray, urlArray, 'cmdTariff', 'POST');
                	                },100);
                	            } 
                		return;
                    //}// end of Group Command  */ 
            		  
            		//비동기 설정
                    $.ajaxSetup({
                        async : true,
                        timeout: 300000
                    });
            		  
            		  searchWin.close();
            		  Ext.Msg.wait('Waiting for response.', 'Wait !');
    	              $.getJSON('${ctx}/gadget/device/command/cmdSetTariffForPKS.do'
    	                      , {
    	                    	'target' : meterId,
    	                      	'mcuId' : mcuId,
  	  	            	  	'tariffTypeCode' : tariffType,
  	  	            	  	'cmd' : 'cmdSetTariff',
    	                      	'loginId' : loginId
    	              }, function(returnData) {
    	            	$.ajaxSetup({
    	      				async : false
  	  	      		});
  	  	      		$('#commandResult').val(returnData.rtnStr);
  	  	      		Ext.Msg.hide();
  	  	      		if (returnData.rtnStr == 'SUCCESS') {
  	  	      			document.getElementById("detail_view_tariff").innerHTML = returnData.detail;
  	  	      			if (returnData.detail != null && returnData.detail != "<html></html>") {
  	  	    	                 $('#detail_dialog_tariff').dialog('open');
  	  	                  }
  	  	              } 
                    });
            	  }
              }
            }, {
              text: '<fmt:message key="aimir.cancel"/>',
              handler: function() {
            	  searchWin.close();
              }
            }]
          });

          searchWin.show(this);
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
						<td class="padding-r20px"><select id="sMeterType"
							style="width: 190px;" name="select">
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
						<td class="padding-r20px"><input type="text" id="sMdsId"
							style="width: 190px;" /></td>

						<!-- Group -->
						<td class="withinput" style="width: 100px"><fmt:message
								key="aimir.metergroup" /></td>
						<td class="padding-r20px"><select id="sMeterGroup"
							name="select" style="width: 120px;">
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
						<td class="padding-r20px"><select id="sStatus" name="select"
							style="width: 190px;">
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
						<td class="padding-r20px"><select id="sModel" name="select"
							style="width: 120px;">
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

						<!-- Modem ID로 검색 추가-->
						<td class="withinput"><fmt:message key="aimir.modemid" /></td>
						<td class="padding-r20px"><input id='sDeviceSerial' type="text"
							style="width: 190px" /></td>
							
						<td class="withinput"><fmt:message key="aimir.modem" /></td>
						<td class="padding-r20px"><select id="sModemYN" name="select"
							style="width: 120px;">
								<option value=""><fmt:message key="aimir.all" /></option>
								<option value="Y"><fmt:message key="aimir.yes" /></option>
								<option value="N"><fmt:message key="aimir.no" /></option>
						</select></td>
						<td class="withinput"><fmt:message key="aimir.customer" /></td>
						<td class="padding-r20px"><select id="sCustomerYN"
							name="select" style="width: 120px;">
								<option value=""><fmt:message key="aimir.all" /></option>
								<option value="Y"><fmt:message key="aimir.yes" /></option>
								<option value="N"><fmt:message key="aimir.no" /></option>
						</select></td>
						
					</tr>
					<tr>
						<td class="withinput"><fmt:message key="aimir.customerid" /></td>
						<td class="padding-r20px"><input id='sCustomerId' type="text"
							style="width: 190px" /></td>
						<td class="withinput"><fmt:message key="aimir.customername" /></td>
						<td class="padding-r20px"><input id='sCustomerName'
							type="text" style="width: 190px" /></td>

						<!-- contract no -->
						<td class="withinput"><fmt:message key="aimir.contractNumber" /></td>

						<td class="padding-r20px"><input id='sConsumLocationId'
							type="text" style="width: 120px" /></td>
						<td class="withinput"><fmt:message
								key="aimir.customeraddress" /></td>
						<td class="padding-r20px"><input id='sMeterAddress'
							type="text" style="width: 190px" /></td>
					</tr>
					<tr>
						<td class="withinput"><fmt:message key="aimir.shipment.gs1" /></td>
						<td class="padding-r20px"><input id='sGs1' type="text"
							style="width: 190px" /></td>
					
						<td class="withinput"><fmt:message key="aimir.nm.mbusSlaveIoModule" /></td>
						<td class="padding-r20px"><select id="sMbusSMYN" name="select"
							style="width: 190px;">
								<option value=""><fmt:message key="aimir.all" /></option>
								<option value="Y">Enable</option>
								<option value="N">Disable</option>
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
						<td class="padding-r20px"><span><input
								id="sLastcommStartDate" class="day" type="text"></span> <span><input
								value="~" class="between" type="text"></span> <span><input
								id="sLastcommEndDate" class="day" type="text"></span> <input
							id="sLastcommStartDateHidden" type="hidden"> <input
							id="sLastcommEndDateHidden" type="hidden"></td>
						
					</tr>
					<tr>
						<td colspan="8" align="right"><em class="am_button"> <a
								href="javascript:reset();"><fmt:message
										key="aimir.form.reset" /></a>
						</em>&nbsp; <em class="am_button"> <a
								href="javascript:searchMeter()" class="on"><fmt:message
										key="aimir.button.search" /></a>
						</em></td>
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
		<div class="bodyleft_meterchart" id="chartPadding">
			<div id="fcPieChartDiv" style="margin-bottom: 7px; float: top;">
				The chart will appear within this DIV. This text will be replaced by
				the chart.</div>

			<ul>
				<li><a href="#" onClick="openCommInfoExcelReport();"
					class="btn_blue" style="float: right;"> <span
						style="padding-bottom: 5px;"><fmt:message
								key="aimir.button.excel" /></span>
				</a></li>
			</ul>

			<div class="gadget_body2">
				<div id="meterSearchChart"></div>
			</div>
		</div>

		<div class="bodyright_meterchart">
			<ul>
				<li class="bodyright_meterchart_leftmargin">
					<!-- 검색결과 정렬방식 (S) --> <!-- todo: "전체" or 아래 내용을 감싸는 div필요 --> <!-- :: <fmt:message key="aimir.all"/> -->

					<div id="search-default" style="height: 26px; margin-bottom: -2px;">
						<ul class="search-modem">
							<li class="withinput"><fmt:message key="aimir.filtering" /></li>
							<li>
								<!-- todo: 검색종류 추가  / 조회조건 --> <!--  필터링  --> <select
								id="sCommState" onChange="javascript:renderGrid();"
								Style="width: 170px;">
									<option value="" style='color: red'><fmt:message key="aimir.all" /></option>
									<option value="0"><fmt:message key="aimir.commstateGreen" /></option>
									<option value="1"><fmt:message key="aimir.commstateYellow" /></option>
									<option value="2"><fmt:message key="aimir.commstateRed" /></option>
							</select>
							</li>
							<li class="space10"></li>
							<li><select id="sOrder" onChange="javascript:renderGrid();"
								Style="width: 210px;">
									<option value="1"><fmt:message key="aimir.mcu.lastcomm" /><fmt:message key="aimir.search.desc" /></option>
									<option value="2"><fmt:message key="aimir.mcu.lastcomm" /><fmt:message key="aimir.search.asc" /></option>
									<option value="3"><fmt:message key="aimir.installationdate" /><fmt:message key="aimir.search.desc" /></option>
									<option value="4"><fmt:message key="aimir.installationdate" /><fmt:message key="aimir.search.asc" /></option>
							</select></li>


							<span style="float: right">
								<li><a href="#" onClick="groupCommandService();"
									class="btn_green"> <span><fmt:message
												key="aimir.group" /> <fmt:message
												key="aimir.instrumentation" /></span>
								</a></li>
								<li><a href="#" onClick="openExcelReport();"
									class="btn_blue"> <span><fmt:message
												key="aimir.button.excel" /></span>
								</a></li>
							</span>
						</ul>
						<%--
                        <ul style="margin-top:2px">
                                                  <li><a id="groupOndemandBtn" href="#" onClick="groupOndemandService();"
                                class="btn_blue"><span><fmt:message key="aimir.groupondemand" /><!-- Group Ondemand --></span></a></li>
                        <li><a id="groupLimitPowerUsage" href="#" onClick="groupLimitPowerUsageService();"
                                class="btn_blue"><span><fmt:message key="aimir.grouplimitpowerusage" /></span></a></li>
                          <li><a id="groupOTABtn" href="#" onClick="groupOTAService();"
                                class="btn_blue"><span><fmt:message key="aimir.groupota" /></span></a></li>
                         <li><a id="groupRelayOff" href="#" onClick="groupRelayOffService();"
                                class="btn_blue"><span><fmt:message key="aimir.grouprelayoff" /></span></a></li>
                          <li><a id="groupRelayO" href="#" onClick="groupRelayOnService();"
                                class="btn_blue"><span><fmt:message key="aimir.grouprelayon" /></span></a></li>

                        </ul>--%>

					</div> <!-- 검색결과 정렬방식 (E) --> <!-- ExtJs : meterSearchGrid (S) -->
					<div id="meterSearchGrid"></div> <!-- ExtJs : meterSearchGrid (E) -->
				</li>
			</ul>
		</div>

	</div>

	<!-- Tab 1,2,3,4,5 (S) -->
	<div id="meterDetailTab">
		<ul>
			<li><a href="#general" id="_general"><fmt:message
						key="aimir.general" /></a></li>
			<!--  <li><a href="#meterInfo" id="_meterInfo"><fmt:message key="aimir.meter.info"/></a></li>  -->
			<li><a href="#locationInfo" id="_locationInfo"><fmt:message
						key="aimir.location.info" /></a></li>

			<%-- <li><a href="#schedule" id="_schedule"><fmt:message
						key="aimir.schedule" /></a></li>	 --%>

			<!-- <li><a href="#history" id="_history"><fmt:message key="aimir.history"/></a></li>  -->
			<li><a href="#measurement" id="_measurement"><fmt:message
						key="aimir.measurement" /></a></li>
			<li><a href="#asyncHistory" id="_asyncHistory">Async Command
					History</a></li>
			<!-- <li><a href="#collectMeterValues" id="_collectMeterValues">Collect
					Meter Values by Real Time</a></li>	 -->
			<li><a href="#netstationMonitoring" id="_netstationMonitoring"><fmt:message key="aimir.nm.netstationMonitoring"/></a></li>
		</ul>

		<!-- Tab 1 : modemDetailTab (S) -->
		<div id="general" class="tabcontentsbox">
			<ul>
				<li>
					<!-- search-default (S) -->
					<div class="blueline" style="min-height: 470px; height: auto;">
						<ul class="width" id="meterInfoArea" style="padding-bottom: 330px">
							<li class="padding">
								<!--  JSP DIV -->
								<div id="meterInfoDiv" class="bodyleft_meterinfo"
									style="height: 0"></div> <!--  JSP Div 종료 -->
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
								<div id="meterCommand" style="padding-top: 20px;">
									<!--  ondemand 버튼 있는 곳 -->
									<!-- 3rd : 명령 (S) -->
									<div class="headspace floatleft">
										<div class="floatleft margin-r5">
											<LABEL class="check"><fmt:message
													key="aimir.instrumentation" /></LABEL>
										</div>
										<br /> <br />
										<!-- 날짜 검색 조건을 받는 Ondemand 버튼 -->
										<div id="onDemandButton" class="floatleft margin-r5" style="display: none">
											<div class="floatleft">
												<em class="btn_org"> <a href="#this" onClick="showOnDemand();"><fmt:message key="aimir.onDemand.Metering" /></a></em> 
											</div>

											<div id="selectDate" class="floatleft tootipbox" style="display: none;">
												<!-- <div id="selectDateForDcu" style="display: inline-block; float: left;">
													<span style="margin: 2px; padding: 0px;"> <input type="radio" name="chk_info" value="MCU">DCU </span>
												</div>

												<span style="margin: 2px; padding: 0px;"> <input type="radio" name="chk_info" value="MODEM">MODEM </span> 
												<span style="margin: 2px; padding: 0px;"> <input type="radio" name="chk_info" value="METER">METER </span> -->
												<span style="margin: 0px; padding: 0px;">
													<button id="onDemandFromDateLeft" type="button" class="back"></button>
												</span> 
												<span style="margin: 0px; padding: 0px;"> 
													<input id="onDemandFromDate" type="text" readonly="readonly" class="day" /> 
													<input id="onDemandFromDateHidden" type="hidden" />
												</span> 
												<span style="margin: 0px; padding: 0px;">
													<button id="onDemandFromDateRight" type="button" class="next"></button>
												</span> 
												<span style="margin-left: 0px; margin-right: 0px; padding-left: 0px; padding-right: 0px;">
													<input value="~" class="between" type="text" />
												</span> 
												<span style="margin: 0px; padding: 0px;">
													<button id="onDemandToDateLeft" type="button" class="back"></button>
												</span> 
												<span style="margin: 0px; padding: 0px;"> 
													<input id="onDemandToDate" type="text" readonly="readonly" class="day" /> 
													<input id="onDemandToDateHidden" type="hidden" />
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

										<div id="OBISButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:popupOBIS();"><fmt:message key="aimir.obis" /></a></em> 
										</div>
										<div id="PingButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:commandPing();"><fmt:message key="aimir.ping" /></a></em> 
										</div>
										<div id="TracerouteButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:commandTraceroute();"><fmt:message key="aimir.traceroute" /></a></em>
												<!-- <em class="btn_org"><a href="javascript:realTimeMeteringTest()">RealTimeMetering Test</a></em> -->
										</div>
												
										<div id="firmWareUpdateButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a id='fwUpload'>Firmware Update</a> </em>
										</div>

										<div id="restoreDefaultFirmwareButton" class="floatleft margin-r5" style="display: none;">
											<em class="btn_org"><a href="javascript:cmdRestoreDefaultFW();">Restore Default Firmware</a></em>
										</div>

										<div id="demandResetButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdDemandReset();">Demand Reset</a></em>
										</div>

										<div id="valveControlButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdRelayStatus();">Meter Status</a></em> 
											<em class="btn_org"><a href="javascript:cmdRelayActivate();">Valve Standby</a></em> 
											<em class="btn_org"><a href="javascript:cmdRelayOff();">Valve Off</a></em> 
											<em class="btn_org"><a href="javascript:cmdRelayOn();">Valve On</a></em>
										</div>
										<div id="relayActivateButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdRelayActivate();">Relay Activate</a></em>
										</div>
										<div id="cmdRelayStatusButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdRelayStatus();">Relay Status</a></em>
										</div>
										<div id="cmdRelayOffButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdRelayOff();">Relay Off</a></em>
										</div>
										<div id="cmdRelayOnButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdRelayOn();">Relay On</a></em>
										</div>
										<%--<div id="relayControlButton" class="floatleft margin-r5">
                                            <em class="btn_org"><a href="javascript:cmdRelayStatus();">Relay Status</a></em>
                                            <em class="btn_org"><a href="javascript:CaptchaPanel('off');">Relay Off</a></em>
                                            <em class="btn_org"><a href="javascript:CaptchaPanel('on');">Relay On</a></em>
                                        </div>--%>
										<div id="meterTimeSyncButton" class="floatleft margin-r5">
											<em class="btn_org"><a href="javascript:cmdMeterTimeSync();">Meter Time Sync</a></em>
										</div>
										<div id="limitPowerUsageButton" class="floatleft margin-r5">
											<em class="btn_org"><a href="javascript:cmdLimitPowerUsage();">
											<fmt:message key="aimir.limitPowerUsage" /></a></em>
										</div>
										<div id="otaButton" class="floatleft margin-r5">
											<em class="btn_org"><a href="javascript:runOta();"><fmt:message key="aimir.ota" /></a></em>
										</div>
										<div id="fwVersionButton" class="floatleft margin-r5">
											<em class="btn_org"><a href="javascript:commandGetMeterFWVersion();">F/W Version</a></em>
										</div>
										<div id="coapPingButton" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:commandCOAPPing();">
											<fmt:message key="aimir.coapPing" /></a></em>
										</div>
										<div id="btnTOUCalendar" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdTOUCalendar();">TOU Calendar</a></em>
										</div>
										<div id="btnDisplayItemSetting" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdDisplayItemSetting();">Display Item Setting</a></em>
										</div>
										<div id="summerTime" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdSummerTime();">Summer Time</a></em>
										</div>
										<div id="energyLevel" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdRelayStatus();">Relay Status</a></em> 
											<em class="btn_org"><a href="javascript:cmdRelayOff();">Relay Off</a></em> 
											<em class="btn_org"><a href="javascript:cmdRelayOn();">Relay On</a></em>
										</div>
										<div id="meterEvent" class="floatleft margin-r5" style="display: none">
											<em class="btn_org"><a href="javascript:cmdGetMeterEvent();">Get Meter Event</a></em>
										</div>
										<div id="billing" class="floatleft margin-r5"
											style="display: none">
											<em class="btn_org"><a href="javascript:cmdGetBilling();">Get Billing Register</a></em>
										</div>
										<!--  <div class="floatleft margin-r5">
                                            <em class="btn_org"><a href="javascript:drawUploadPanel();">OTA</a></em>
                                        </div> -->
                                        <div id="meterCmd" class="floatleft margin-r5" style="display: none">                     
                                          <!--   <em class="btn_org"><a href="javascript:commandGetDemandPeriod();">Get Demand Period</a></em>                   	
                                            <em class="btn_org"><a href="javascript:commandDemandPeriod();">Set Demand Period</a></em>
                                            <em class="btn_org"><a href="javascript:commandGetMeterFWVersion();">Get Meter F/W Version</a></em>
                                            <em class="btn_org"><a href="javascript:commandGetBilingCycle();">Get Biling Cycle</a></em>
                                            <em class="btn_org"><a href="javascript:commandSetBilingCycle();">Set Biling Cycle</a></em> -->
                                            
                                            <!-- <em class="btn_org"><a href="javascript:cmdTOUSet();">TOU</a></em>   -->
                                        </div>
                                        <div id="getTariff" class="floatleft margin-r5" style="display: none">
                                            <em class="btn_org"><a href="javascript:cmdGetTariff();">Get Tariff</a></em>
                                        </div>
                                        <div id="setTariff" class="floatleft margin-r5" style="display: none">
                                            <em class="btn_org"><a href="javascript:cmdSetTariffForPKS();">Set Tariff</a></em>
                                        </div>
                                        <div id="getCurrnetLoadLimit" class="floatleft margin-r5" style="display: none">
                                        	<em class="btn_org"><a href="javascript:commandGetCurrentLoadLimit();">Get Current Load Limit</a></em>
                                        </div>
                                        <div id="setCurrnetLoadLimit" class="floatleft margin-r5" style="display: none">
                                        	<em class="btn_org"><a href="javascript:commandSetCurrentLoadLimit();">Set Current Load Limit</a></em>
                                        </div>
									</div>

									<div class="meterinfo-textarea clear">
										<ul>
											<li><textarea id="commandResult" readonly>Result</textarea></li>
										</ul>
									</div>
									<div id="detail_dialog" class="mvm-popwin-iframe-outer"
										title="ondemand">
										<div id="detail_view" style="overflow-y: auto"></div>
									</div>
									<!-- 3rd : 명령 (E) -->
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

		<!-- Tab 2 : locationInfo (S) -->
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
														class="am_button"> <a
															href="javascript:updateMeterLoc()" class="on"><fmt:message
																	key="aimir.mcu.coordinate.update" /></a>
													</em></td>
													<td align="right"><em class="am_button"> <a
															href="javascript:updateMeterAddress()" class="on"><fmt:message
																	key="aimir.mcu.adress.update" /></a>
													</em> <em class="am_button"> <a
															href="javascript:getGeoCoding();" class="on"><fmt:message
																	key="aimir.address" />-<fmt:message
																	key="aimir.mcu.coordinate" /></a>
													</em></td>
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
		<!-- Tab 2 : locationInfo (E) -->

		<!-- Tab 3 : schedule (S) -->
	<!-- NI Command hide - 2019.12.24
		<div id="schedule" class="tabcontentsbox">
			Sub page will be loaded (device/meterMaxGadgetScheduleTab.jsp)
		</div> 		-->
		<!-- Tab 3 : schedule (E) -->

		<!-- Tab 4 : measurement (S) -->
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
							<li class="padding" style="height: 250px;" id="chartPadding">
								<div class="flexlist">
									<div id="fcMeasureChartDiv">The chart will appear within
										this DIV. This text will be replaced by the chart.</div>
								</div>

							</li>
							<li>
								<!-- ExtJs : meterMeteringGrid (S) -->
								<div id="meterMeteringGrid" style="float: top;"></div> <!-- ExtJs : meterMeteringGrid (E) -->
							</li>
						</ul>
					</div> <!-- search-default (E) -->

				</li>
			</ul>
		</div>
		<!-- Tab 4 : measurement (E) -->

		<!-- Tab 5 : Async Command History (S) -->
		<div id="asyncHistory" class="tabcontentsbox">
			<ul>
				<li>
					<div class="blueline" style="height: 400px;">
						<!-- Search Date (S) -->
						<div class="width-100"
							style="padding-top: 20px; padding-left: 20px; padding-right: 20px; padding-bottom: 20px;">
							<label class="check">Async Comm. Log</label>
							<div class="width-auto margin-t10px">
								<span class="margin-t5px margin-r5">Search Date</span> <span><input
									id="asyncCommandHistoryStartDate" class="day" type="text"></span>
								<span><input value="~" class="between" type="text"></span>
								<span class="margin-r5"><input
									id="asyncCommandHistoryEndDate" class="day" type="text"></span>
								<span class="margin-t2px am_button"><a
									href="javascript:getAsyncHistoryGrid()"><fmt:message
											key="aimir.button.search" /></a></span> <input type="hidden"
									id="asyncCommandHistoryStartDateHidden" /> <input
									type="hidden" id="asyncCommandHistoryEndDateHidden" />
							</div>
						</div>
						</br> </br>
						<!-- Search Date (E) -->
						<!-- Command History (S) -->
						<div id="padding-10"
							style="padding-left: 10px; padding-right: 10px;">
							<div id="asyncHistoryGrid" class="tabcontentsbox"></div>
						</div>
						<!-- Command History (E) -->
					</div>
				</li>
			</ul>
		</div>
		<!-- Tab 5 : Async Command History (E) -->

		<!-- Tab 6 : Collect Meter Values (S) -->
	<%-- 	NI Command hide - 2019.12.24
		<div id="collectMeterValues" class="tabcontentsbox">
			<ul>
				<li>
					<div class="blueline" style="height: 350px;">

						<!-- Search Date (S) -->
						<div class="width-100"
							style="padding-top: 20px; padding-left: 20px; padding-right: 20px; padding-bottom: 20px;">
							<label class="check">Collect Meter Values</label><br />
							<div class="width-auto margin-t10px"></div>

							<table class="searching">
								<tbody>
									<tr>
										<td class="withinput" style="width: 120px"><b>Collection
												Period</b></td>
										<td class="withinput" style="width: 100px"><select
											id="duration" name="duration" style="width: 50px;">
												<option value="1" selected>&nbsp;&nbsp;1</option>
												<option value="2" select>&nbsp;&nbsp;2</option>
												<option value="3" select>&nbsp;&nbsp;3</option>
												<option value="4" select>&nbsp;&nbsp;4</option>
												<option value="5" select>&nbsp;&nbsp;5</option>
												<option value="6" select>&nbsp;&nbsp;6</option>
												<option value="7" select>&nbsp;&nbsp;7</option>
												<option value="8" select>&nbsp;&nbsp;8</option>
												<option value="9" select>&nbsp;&nbsp;9</option>
												<option value="10" select>&nbsp;10</option>
										</select> &nbsp;minute</td>

										<td class="padding-r10px"></td>
										<td class="padding-r10px"></td>
										<td class="padding-r10px"></td>

										<td class="withinput" style="width: 55px"><b>Interval</b></td>
										<td><input id="interval" type="text"
											style="width: 100px; text-align: center;" value="60"></td>
										<td class="padding-r10px"></td>
										<td class="withinput" style="width: 60px">second</td>
										<!-- <td class="withinput" style="width: 40px">Model</td>
							<td><input id="sModelName" type="text" style="width: 70px;"></td> -->
										<td class="padding-r10px"></td>
										<td><em class="am_button margin-t2px"><a
												href="javascript:collectMeterValues();"><fmt:message
														key="aimir.execute" /></a></em></td>
									</tr>
								</tbody>
							</table>

							<div class="width-auto margin-t20px">
								<span class="margin-t5px margin-r5">Search Date</span> <span><input
									id="collectMeterValuesStartDate" class="day" type="text"></span>
								<!-- <span><input value="~" class="between" type="text"></span> -->
								<!-- <span class="margin-r5"><input id="collectMeterValuesEndDate" class="day" type="text"></span> -->
								<span class="margin-t2px margin-l5 am_button"><a
									href="javascript:getCollectMeterValuesGrid()"><fmt:message
											key="aimir.button.search" /></a></span> <input type="hidden"
									id="collectMeterValuesStartDateHidden" />
								<!-- <input type="hidden" id="collectMeterValuesEndDateHidden" /> -->
							</div>
						</div>
						</br> </br>
						<!-- Search Date (E) -->
						<!-- Command History (S) -->
						<div id="padding-10"
							style="padding-left: 10px; padding-right: 10px;">
							<div id="collectMeterValuesGrid" class="tabcontentsbox"></div>
						</div>
						<!-- Command History (E) -->
					</div>
				</li>
			</ul>
		</div>			 --%>
		<!-- Tab 6 : Collect Meter Values (E) -->
		<!-- Tab 7 : Netstation Monitoring (N) -->
		<div id="netstationMonitoring" class="tabcontentsbox">
					<div id="nsCurrentInfoDiv" >
		</div>
		<!-- Tab 7 : Netstation Monitoring (N)  -->
	</div>
</body>
</html>
