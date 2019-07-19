<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><fmt:message key="gadget.system008"/></title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
        /* ext-js grid 내 button 등이 있을 경우 높이조절 */
        .grid-button-height .x-grid3-cell-inner {
            padding-top: 0.5px;
            padding-bottom: 0.5px;
        }
    </style>
    
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.cookie.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.hotkeys.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.metadata.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/sarissa.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.checkbox.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.contextmenu.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.cookie.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.hotkeys.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.metadata.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.themeroller.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.xml_flat.js"></script>
    <script type="text/javascript" src="${ctx}/js/plugins/jquery.tree.xml_nested.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">

        var supplierId =  ${supplierId};
        // Command 권한
        var cmdAuth = "${cmdAuth}";

        var chromeColAdd = 0;
        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        function extColumnResize() {
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
        }
        //$(document).ready(function() {
        Ext.onReady(function() {
            console.log("onReady");
        	Ext.QuickTips.init();
            extColumnResize();

            if (cmdAuth == "true") {
                $("#saveBtnList").show();
            } else {
                $("#saveBtnList").hide();
            }
            getCondition();
            getOperationLogMaxGrid();
            updateFChart();
            getOperationLogDetailGrid();
        });

        //윈도우 리싸이즈시 event
        $(window).resize(function() {
            if ($('#searchDiv').is(':visible')) {
                updateFChart();
                operationLogMaxGrid.destroy();
                operationLogMaxGridOn = false;

                getOperationLogMaxGrid();
                operationLogDetailGrid.destroy();
                operationLogDetailGridOn = false;

                getOperationLogDetailGrid();
            } else if ($('#settingDiv').is(':visible')) {
                if ($('#equipmentDiv').is(':visible')) {
                    getEquipListMaxGrid(equipmentId);
                } else if ($('#operationDiv').is(':visible')) { 
                    getOperationListMaxGrid(operationId);
                }
            }  
        }); 

        //탭초기화
        // 값 0 - 숨김처리
        // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
        var tabs     = {hourly:1,daily:0,period:1,weekly:1,monthly:0,monthlyPeriod:1,weekDaily:0,seasonal:0,yearly:0};

        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

        var equipmentId = '';
        var operationId = '';

        var fcChartDataXml;
        var fcChart;

        var startDate;
        var endDate;
        var operation = '';
        var clickControl = '';
        var operatorType2 = '';
        var status2 = '';
        var date = '';
        var page = 0;
        var changeTreeCheck = 0;
        var browserWidth = "";

        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.ranking"/>";
        fmtMessage[1] = "<fmt:message key="aimir.instrumentation"/>";
        fmtMessage[2] = "<fmt:message key="aimir.total"/> (<fmt:message key="aimir.success"/>/<fmt:message key="aimir.failure"/>)";
        fmtMessage[3] = "<fmt:message key="aimir.orderNo"/>";
        fmtMessage[4] = "<fmt:message key="aimir.opentime"/>";
        fmtMessage[5] = "<fmt:message key="aimir.targetType"/>";
        fmtMessage[6] = "<fmt:message key="aimir.target"/>";
        fmtMessage[7] = "<fmt:message key="aimir.operator"/>" +" " + "<fmt:message key="aimir.type2"/>";
        fmtMessage[8] = "<fmt:message key="aimir.operator"/>";
        fmtMessage[9] = "<fmt:message key="aimir.instrumentation"/>";
        fmtMessage[10] = "<fmt:message key="aimir.status"/>";
        fmtMessage[11] = "<fmt:message key="aimir.description"/>";
        fmtMessage[12] = "<fmt:message key="aimir.firmware.msg09"/>";       //데이터를 찾을수 없습니다.
        fmtMessage[13] = "<fmt:message key="aimir.excel.operationLog"/>";       //Title
        fmtMessage[14] = "<fmt:message key="aimir.equipment"/>";

        $(function() {

            $("#startDate").datepicker({showOn: 'button', buttonImage: '${ctx}/images/calendar.gif', buttonImageOnly: true});
            $("#endDate").datepicker({showOn: 'button', buttonImage: '${ctx}/images/calendar.gif', buttonImageOnly: true});
            var selectedTreeId = '';

            $('#equipmentDiv').tree({
                data : {
                    type : 'json',
                    opts : {
                        method : 'POST',
                        url : '${ctx}/gadget/device/operationLog/getEquipments.do'
                    }
                },
                types   : {
                    "default" : {
                        deletable : true,
                        renameable : true
                    },
                    "device" : {
                        valid_children : ["device"],
                        icon : {
                           image : '${ctx}/js/tree/themes/default/device.gif'
                        }
                    },
                    "deviceType" : {
                        valid_children : ["deviceType"],
                        icon : {
                           image : '${ctx}/js/tree/themes/default/device.gif'
                        }
                    },
                    "deviceVendor" : {
                        valid_children : ["deviceVendor"],
                        icon : {
                           image : '${ctx}/js/tree/themes/default/vendor.gif'

                        }
                    },

                    "deviceModel" : {
                        valid_children : ["deviceModel"],
                        icon : {
                           image : '${ctx}/js/tree/themes/default/model.gif'

                        }
                    }
                },
                callback : {
                    'onselect' : function(n) {

                        var deviceModel = $(n).attr('rel');

                        if (deviceModel == 'deviceModel') {
                            equipmentId = $(n).attr('id');
                            searchOperationList($(n).attr('id'));
                        }

                    },
                    'ondata' : function(data) {

                        return data.jsonTree;
                    }
                }
            });

            $('#operationDiv').tree({
                data : {
                    type : 'json',
                    opts : {
                        method : 'POST',
                        url : '${ctx}/gadget/device/operationLog/getOperations.do'
                    }
                },
                callback : {
                    'onselect' : function(n) {

                        var depth = $(n).attr('depth');

                        if (depth == '2') {
                            operationId = $(n).attr('operationCodeId');
                            searchOperationList($(n).attr('operationCodeId'));
                        }
                    },
                    'ondata' : function(data) {

                        return data.jsonTree;
                    }
                }
            });

            $('#operatorType').selectbox();
            $('#status').selectbox();
            $('#targetType').selectbox();
        });

        var refreshGrid = function() {

            getLevelComboData();
            var treeIds = getTreeIds();
            var deviceModelId = treeIds[0];
            var operationCodeId = treeIds[1];

            if ($('#equipmentDiv').is(':visible') && deviceModelId != '') {
                getEquipListMaxGrid(deviceModelId);
            } else if ($('#operationDiv').is(':visible')&& operationCodeId != '') {
                getOperationListMaxGrid(operationCodeId);
            }
        };

        //검색버튼 클릭시
        var searchList = function() {
            getCondition();
            getOperationLogMaxGrid();
            updateFChart();
            getOperationLogDetailGrid();
        };

        var send = function() {
            searchList();
        };
        //탭 변경시
        var changeDiv = function(_divName) {

            if (_divName == 'searchDiv') {
                $('#searchDiv').show();
                getCondition();
                getOperationLogMaxGrid();
                updateFChart();
                getOperationLogDetailGrid();

                $('#settingDiv').hide();

            } else if (_divName == 'settingDiv') {
                $('#searchDiv').hide();

                $('#settingDiv').show();
                //refreshGrid();
                getEquipListMaxGrid(preEquipModelId);
            }
        };

        //설정 화면 탭 변경시
        var changeTreeDiv = function(_divName) {

            if (_divName == 'equipmentDiv') {

                changeTreeCheck = 0;
                operationListMaxGrid.hide();
                equipListMaxGrid.show();

                $('#equipmentDiv').show();
                $('#operationDiv').hide();
                changeTree(_divName);
            } else if (_divName == 'operationDiv') {
                changeTreeCheck = 1;
                equipListMaxGrid.hide();
                if (operationListMaxGrid != undefined)
                	operationListMaxGrid.show();

                $('#operationDiv').show();
                $('#equipmentDiv').hide();
                changeTree(_divName);
            }
        };

        var changeTree = function(_divName) {

            //refreshGrid();

            var treeIds = getTreeIds();
            if (_divName == 'equipmentDiv') {

                getEquipListMaxGrid(treeIds[0]);

            } else if(_divName == 'operationDiv') {

                getOperationListMaxGrid(treeIds[1]);
            }
        };

        var searchOperationList = function(_id) {

            selectedTreeId = _id;

            //refreshGrid();
            getLevelComboData();
            if ($('#equipmentDiv').is(':visible')) {

                getEquipListMaxGrid(_id);
            } else if ($('#operationDiv').is(':visible')) {

                getOperationListMaxGrid(_id);
            }
        };

        var nullCheck = function(treecheck) {
            var tempInt = 1;
            var data;

            if ($('#equipmentDiv').is(':visible')) {
                data = equipListMaxGridStore.data.length;
            } else if ($('#operationDiv').is(':visible')) {
                data = operationListMaxGridStore.data.length;
            }

            if (treecheck == 0 && data == 0) {
                tempInt = 0;
            }

            if (treecheck == 1 && data == 0) {
                tempInt = 0;
            }

            return tempInt;
        }

        var updateOperationList = function() {

            var getDataStore;
            var arrayCollection;
            var updateParam="";



            if ($('#equipmentDiv').is(':visible')) {
                getDataStore = equipListMaxGridStore;
                arrayCollection = getDataStore.data.items;
            } else if ($('#operationDiv').is(':visible')) {
                getDataStore = equipListMaxGridStore;
                arrayCollection = getDataStore.data.items;
            }

            for (var i = 0; i < arrayCollection.length; i++) {
                var item =  arrayCollection[i].data;

                updateParam += item.id + "_" + item.level + ":";
            }

            // changeTreeCheck :::: equipmentGridDatas = 0 , operationDataGrid = 1
            if (nullCheck(changeTreeCheck) == 0) {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>','<fmt:message key="aimir.updatedata.notexist"/>');
            } else {
                if (confirm('<fmt:message key="aimir.wouldSave"/>')) {
                    var params = {
                        updateParam : updateParam
                    };

                    $.post("${ctx}/gadget/device/operationLog/updateOperation.do",
                           params,
                           function(json) {
                               if (json != null && json.result == "success") {
                                   Ext.Msg.alert("<fmt:message key="aimir.success"/>", "<fmt:message key="aimir.save"/>",
                                                 function() {
                                                     getDataStore.reload();
                                                     getDataStore.rejectChanges();

                                                 });

                               } else {
                                   Ext.Msg.alert("<fmt:message key="aimir.error"/>",
                                                 "<fmt:message key="aimir.save.error"/>");
                               }

                               return;
                    });

                }
            }
        };

        var cancelOperationList = function() {
            refreshGrid();
        };

        var getTreeIds = function() {
            var treeIds = [];
            treeIds[0] = equipmentId;
            treeIds[1] = operationId;
            return treeIds;
        };

        function eventFChart( _operatorType2, _status2, _date) {

            clickControl = 'columnChart';
            operatorType2 = _operatorType2;
            status2 = _status2;
            date = _date;
            page = 0;
            
            if (date.length >= 17) {
                $.getJSON('${ctx}/common/convertDBDateYYYYMMDDHHMMSS.do',
                        {supplierId:supplierId,
                        localDate:_date}
                        ,function(json) {
                            date = json.dbDate;
                            getCondition();
                            var arrayObj4 = arrayObj[4];
                            var arrayObj7 = arrayObj[7];
                            var arrayObj8 = arrayObj[8];
                            if("fail" == status2) {
                                arrayObj[4] = 1;  
                            } else if("success" == status2) {
                                arrayObj[4] = 0;      
                            }

                            arrayObj[7] = date;
                            arrayObj[8] = date.substr(0,10) + "5959";
                            
                            getOperationLogDetailGrid();
                            
                            arrayObj[4] = arrayObj4;
                            arrayObj[7] = arrayObj7;
                            arrayObj[8] = arrayObj8;
                        });
            } else {
                $.getJSON('${ctx}/common/convertSearchDate.do',
                        {supplierId:supplierId,
                         searchStartDate:_date,
                         searchEndDate:_date}
                        ,function(json) {
                            date = json.searchStartDate;
                            getCondition();
                            var arrayObj4 = arrayObj[4];
                            var arrayObj7 = arrayObj[7];
                            var arrayObj8 = arrayObj[8];
                            if("fail" == status2) {
                                arrayObj[4] = 1;  
                            } else if("success" == status2) {
                                arrayObj[4] = 0;      
                            }

                            arrayObj[7] = date;
                            arrayObj[8] = date;
                            
                            getOperationLogDetailGrid();
                            
                            arrayObj[4] = arrayObj4;
                            arrayObj[7] = arrayObj7;
                            arrayObj[8] = arrayObj8;

                });
            }
        }

        function updateFChart() {
            console.log("updateFChart");
        	emergePre();

            $.getJSON('${ctx}/gadget/device/operationLog/getOperationLogMaxChartData.do'
                    ,{
                        operatorType  : arrayObj[0] ,
                        userId        : arrayObj[1],
                        targetType    : arrayObj[2],
                        targetName    : arrayObj[3],
                        status        : arrayObj[4],
                        description   : arrayObj[5],
                        period        : arrayObj[6],
                        startDate     : arrayObj[7],
                        endDate       : arrayObj[8],
                        supplierId    : arrayObj[9],
                        operation     : arrayObj[10],
                        clickControl  : arrayObj[11],
                        operatorType2 : arrayObj[12],
                        status2       : arrayObj[13],
                        date          : arrayObj[14],
                        page          : arrayObj[15],
                        pageSize      : arrayObj[16]
                    }
                    ,function(json) {
                        console.log(json); 
                    	var list = json.chartDatas;
                         fcChartDataXml = '{"chart": {'             	 
                        	 + ' "showvalues": "0", '
                        	 + ' "drawcrossline": "1", '
                        	 + ' "baseFont": "Helvetica", '
                        	 + ' "baseFontSize": "12", '
                        	 + ' "PlotTooltext": "<b>[$label]</b> <br> $seriesName = <b>$dataValue</b> ($percentValue)", '
                        	 + ' "toolTipBgColor": "#E9F2FF", '
                        	 + ' "scrollheight": "12", '
                        	 + ' "numvisibleplot": "20", ' 
                        	 + ' "labelDisplay": "rotate", '
                        	 + ' "slantLabel": "1", '
                        	 + ' "maxLabelHeight": "90",  '
                        	 + ' "interactiveLegend": "1", '
                        	 + ' "plotgradientcolor": "", '
                        	 + ' "formatnumberscale": "1", '
                        	 + ' "showplotborder": "0", '
                        	 + ' "palettecolors": "#72d572,#f69988", '
                        	 + ' "canvaspadding": "0", '
                        	 + ' "bgcolor": "FFFFFF", '
                        	 + ' "showalternatehgridcolor": "1", '
                             + ' "alternateHGridColor": "#fafafa", '
                        	 + ' "divlinecolor": "CCCCCC", '
                        	 + ' "showcanvasborder": "0", '
                        	 + ' "legendborderalpha": "0", '
                        	 + ' "legendshadow": "0", '
                        	 + ' "showpercentvalues": "0", '
                        	 + ' "showsum": "1", '
                        	 + ' "canvasborderalpha": "0", '
                        	 + ' "showborder": "1", '
                        	 + ' "forceYAxisValueDecimals": "0", '
                             + ' "borderColor": "#99bbe8", '
                             + ' "borderThickness": "1", '
                             + ' "borderAlpha": "100", '
                             + ' "showHoverEffect": "1" '
                        	 + ' }, ';

                        var categories = ' "categories": [{"category": [ ';
                        var dataset1 = ' "dataset": [{"seriesname": "<fmt:message key="aimir.success.count"/>", "renderas": "Area", "data": [ ';
                        var dataset2 = ' {"seriesname": "<fmt:message key="aimir.failure.count"/>", "renderas": "Area", "data": [ ';


                        for(var i = 0; i < list.length; i++){
                        	var index = list[i];
                        	console.log("list["+i+"]", index);
                            if(index != "indexOf") {
                                if(i != 0) {
                                	categories += ',';
                                	dataset1 += ',';
                                	dataset2 += ',';
                                }
                                categories += ' { "label": "'+index.date+'", "stepSkipped": false, "appliedSmartLabel": true }';
                                dataset1 += (index.successCnt > 0) ? '{"value":"' + index.successCnt + '"}' : '{"value":""}'; 
                                dataset2 += (index.failCnt > 0) ? '{"value":"' + index.failCnt + '"}' : '{"value":""}'; 
                            	
                            }
                        }
                        categories += ']}],';
                        dataset1 += ']},';
                        dataset2 += ']}]';

                        fcChartDataXml += categories + dataset1 + dataset2 + '}';
						//console.log(fcChartDataXml);
                        fcChartRender();

                        hide();
                    }
            );
        }
        
        function fcChartRender() {
            if ($ ('#fcChartDiv').is(':visible')) {
                fcChart = new FusionCharts({
        			type: 'scrollstackedcolumn2d',
        			renderAt : 'fcChartDiv',
        			width : $('#fcChartDiv').width(),
        			height : '284',
        			dataFormat : 'json',
        			dataSource : fcChartDataXml
        		}).render();
            }
        }

        var winOperationLog;
        function openExcelReport() {

            var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            var fmtMessage1 = new Array();

            fmtMessage1[0] =  fmtMessage[3];
            fmtMessage1[1] =  fmtMessage[4];
            fmtMessage1[2] =  fmtMessage[5];
            fmtMessage1[3] =  fmtMessage[6];
            fmtMessage1[4] =  fmtMessage[7];

            fmtMessage1[5] =  fmtMessage[8];
            fmtMessage1[6] =  fmtMessage[9];
            fmtMessage1[7] =  fmtMessage[10];
            fmtMessage1[8] =  fmtMessage[11];
            fmtMessage1[9] =  fmtMessage[12];

            fmtMessage1[10] = fmtMessage[13];

            obj.condition = arrayObj;
            obj.fmtMessage1 = fmtMessage1;

            if(winOperationLog)
                winOperationLog.close();
            winOperationLog = window.open("${ctx}/gadget/device/operationLogMaxExcelDownloadPopup.do",
                            "OperationLogExcel", opts);
            winOperationLog.opener.obj = obj;

        }

        var arrayObj = Array();
        //검색 조건.
        var getCondition = function() {

            arrayObj[0] = $('#operatorType').val();//operatorType
            arrayObj[1] = $('#userId').val();      // userId
            arrayObj[2] = $('#targetType').val();  //targetType
            arrayObj[3] = $('#targetName').val();  //targetName
            arrayObj[4] = $('#status').val();      //status
            arrayObj[5] = ''; //description


            var searchDateType = $('#searchDateType').val();

            arrayObj[6] = searchDateType;       //period

            var searchStartDate = $('#searchStartDate').val();
            var searchEndDate = $('#searchEndDate').val();
            var searchStartHour = $('#searchStartHour').val();
            var searchEndHour = $('#searchEndHour').val();

            if (searchDateType == DateType.HOURLY) {
                arrayObj[7] = searchStartDate + searchStartHour + "0000"; //startDate
                arrayObj[8] = searchEndDate + searchEndHour + "5959";     //endDate
            }  else {
                arrayObj[7] = searchStartDate + "000000";
                arrayObj[8] = searchEndDate + "235959";
            }

            arrayObj[9] = supplierId; //supplierId

            arrayObj[10] = ''; //operation
            arrayObj[11] = ''; //clickControl
            arrayObj[12] = ''; //operatorType2
            arrayObj[13] = ''; //status2
            arrayObj[14] = ''; //date
            arrayObj[15] = 0;//page
            arrayObj[16] = 10; //pageSize

            //console.log(arrayObj);
        };

        function renderGph(value, metadata, record, rowIndex, colIndex, store) {

            var record = store.data.items[rowIndex].data;
            var percentage = (record.successCnt.replace(",","")  / record.cnt.replace(",","")) * 100;
            var w = Math.floor(percentage);

            var html = '<div style="margin: 2px 18px 2px 5px; height: 18px;">'+
                       '<div class="x-progress-wrap" style="height: 3px;">'+
                       '<div class="x-progress-inner">'+
                       '<div class="x-progress-bar" style="width:'+w+'%; height: 3px !important;">'+
                       '</div>'+
                       '</div>'+
                       '</div>'+
                       '<div class="x-progress-text x-progress-text-back" style="position: static !important; font-size: 8px !important;">'+
                       '<div>' + record.cnt +'('+record.successCnt+ '/'+record.failCnt+')'+'</div>'+
                       '</div>'+
                       '</div>';

            return html;
        }

        var operationLogMaxGridStore;
        var operationLogMaxGridColModel;
        var operationLogMaxGridOn = false;
        var operationLogMaxGrid;

        function getOperationLogMaxGrid() {

            var width = $("#operationLogMaxGridDiv").width();
            operationLogMaxGridStore = new Ext.data.JsonStore({
                autoLoad : true,
                url : "${ctx}/gadget/device/operationLog/getOperationLogMaxAdvanceData.do",
                baseParams : {
                    operatorType  : arrayObj[0] ,
                    userId        : arrayObj[1],
                    targetType    : arrayObj[2],
                    targetName    : arrayObj[3],
                    status        : arrayObj[4],
                    description   : arrayObj[5],
                    period        : arrayObj[6],
                    startDate     : arrayObj[7],
                    endDate       : arrayObj[8],
                    supplierId    : arrayObj[9],
                    operation     : arrayObj[10],
                    clickControl  : arrayObj[11],
                    operatorType2 : arrayObj[12],
                    status2       : arrayObj[13],
                    date          : arrayObj[14],
                    page          : arrayObj[15],
                    pageSize      : 5
                },
                root : 'advanceGridDatas',
                fields : [
                    {name: 'rank', type: 'String'},
                    {name: 'cnt', type: 'String'},
                    {name: 'failCnt', type: 'String'},
                    {name: 'operation', type: 'String'},
                    {name: 'operationCommandId', type: 'Integer'},
                    {name: 'successCnt', type: 'String'} ,
                    {name: 'systemCnt', type: 'Integer'} ,
                    {name: 'userCnt', type: 'Integer'} ,
                    {name: 'width', type: 'Integer'}
                ]
            });

            var colWidth = (width-70)/2 - chromeColAdd;
            operationLogMaxGridColModel = new Ext.grid.ColumnModel({
                columns : [
                    {header: fmtMessage[0],
                     dataIndex: 'rank',
                     width: 70 - chromeColAdd
                    }
                   ,{header: fmtMessage[1],
                     dataIndex: 'operation',
                     width: colWidth + 20,
                     renderer: addTooltip
                    }
                   ,{header: fmtMessage[2],
                     dataIndex: 'successCnt',
                     width: colWidth - 20,
                     tooltip: fmtMessage[2],
                     renderer: renderGph
                    }
                ],
                defaults : {
                     sortable: true
                    ,menuDisabled: true
                    ,width: colWidth
                    ,align: 'center'
                }
            });

            if (operationLogMaxGridOn == false) {
                operationLogMaxGrid = new Ext.grid.GridPanel({
                    id : 'operationLogMaxGrid',
                    store : operationLogMaxGridStore,
                    colModel : operationLogMaxGridColModel,
                    autoScroll : false,
                    width : width,
                    height : 284,
                    stripeRows : true,
                    selModel : new Ext.grid.RowSelectionModel({
                        listeners : {
                            rowselect : function(selectionModel, columnIndex, value) {
                                arrayObj[10]=value.data.operationCommandId;
                                getOperationLogDetailGrid();
                            }
                        }
                    }),
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'operationLogMaxGridDiv',
                    viewConfig : {
                        forceFit : true,
                        //scrollOffset : 1,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    }
                });
                operationLogMaxGridOn = true;
            } else {
                operationLogMaxGrid.setWidth(width);
                operationLogMaxGrid.reconfigure(operationLogMaxGridStore, operationLogMaxGridColModel);
            }
        };

        var operationLogDetailGridStore;
        var operationLogDetailGridColModel;
        var operationLogDetailGridOn = false;
        var operationLogDetailGrid;

        function getOperationLogDetailGrid(){

            var width = $("#operationLogDetailGridDiv").width();

            operationLogDetailGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/device/operationLog/getOperationLogMaxGridData.do",
                baseParams:{
                    operatorType  : arrayObj[0] ,
                    userId        : arrayObj[1],
                    targetType    : arrayObj[2],
                    targetName    : arrayObj[3],
                    status        : arrayObj[4],
                    description   : arrayObj[5],
                    period        : arrayObj[6],
                    startDate     : arrayObj[7],
                    endDate       : arrayObj[8],
                    supplierId    : arrayObj[9],
                    operation     : arrayObj[10],
                    clickControl  : arrayObj[11],
                    operatorType2 : arrayObj[12],
                    status2       : arrayObj[13],
                    date          : arrayObj[14],
                    pageSize      : arrayObj[16]
                },
                totalProperty: 'total',
                root:'gridDatas',
                fields: [
                    { name: 'no', type: 'String' },
                    { name: 'openTime', type: 'String' },
                    { name: 'targetType', type: 'String' },
                    { name: 'targetName', type: 'String' },
                    { name: 'accomplishmentType', type: 'String' },
                    { name: 'accomplisher', type: 'String' } ,
                    { name: 'operation', type: 'String' } ,
                    { name: 'operationStatus', type: 'String' }
                ],
                listeners: {
                    beforeload: function(store, options){
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                        });
                    }
                }
            });

            var colWidth = (width-77)/7 - chromeColAdd;
            operationLogDetailGridColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: fmtMessage[3],
                     dataIndex: 'no',
                     width: 77 - chromeColAdd
                    }
                   ,{header: fmtMessage[4],
                     dataIndex: 'openTime',
                     width: colWidth - 17
                    }
                   ,{header: fmtMessage[5],
                     dataIndex: 'targetType',
                     width: colWidth - 12
                    }
                   ,{header: fmtMessage[6],
                     dataIndex: 'targetName',
                     width: colWidth - 12
                    }
                   ,{header: fmtMessage[7],
                     dataIndex: 'accomplishmentType',
                     width: colWidth - 12
                    }
                   ,{header: fmtMessage[8],
                     dataIndex: 'accomplisher',
                     width: colWidth - 12
                    }
                   ,{header: fmtMessage[9],
                     dataIndex: 'operation',
                     width: colWidth - 12
                    }
                   ,{header: fmtMessage[10],
                     dataIndex: 'operationStatus',
                     width: colWidth + 77 - 4
                    }
                ],
                defaults : {
                     sortable: true
                    ,menuDisabled: true
                    ,width: colWidth
                    ,align: 'center'
                    ,renderer: addTooltip
                },
            });
            if (operationLogDetailGridOn == false) {
                operationLogDetailGrid = new Ext.grid.GridPanel({
                    id : 'operationLogDetailGrid',
                    store : operationLogDetailGridStore,
                    colModel : operationLogDetailGridColModel,
                    autoScroll : false,
                    width : width,
                    height : 290,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'operationLogDetailGridDiv',
                    viewConfig : {
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    },
                    bbar : new Ext.PagingToolbar({
                        pageSize : 10,
                        store : operationLogDetailGridStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
                    })
                });
                operationLogDetailGridOn = true;
            } else {
                operationLogDetailGrid.setWidth(width);
                operationLogDetailGrid.reconfigure(operationLogDetailGridStore, operationLogDetailGridColModel);
                var bottomToolbar = operationLogDetailGrid.getBottomToolbar();
                bottomToolbar.bindStore(operationLogDetailGridStore);
            }
        }

        var LevelData =["disable","SYSTEM","OPERATOR", "CUSTOMER"];
        var displayTypeComboData = [];
        var combo;

        var getLevelComboData = function() {

            for (var d = 0; d < LevelData.length; d++) {
                displayTypeComboData.push({
                id: d,
                name: LevelData[d]});
            }

            combo = new Ext.form.ComboBox({
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender:true,
                    mode: 'local',
                    width : 258,
                    store: new Ext.data.JsonStore({
                        id: 0,
                        data: displayTypeComboData,
                        fields: ["id", "name"]
                    }),
                    valueField: "id",
                    displayField: "name",
                    editable: false
            });
        }

        // create reusable renderer
        Ext.util.Format.comboRenderer = function(combo){
            return function(value) {
                var record = combo.findRecord(combo.valueField, value);
                return record ? record.get(combo.displayField) : combo.valueNotFoundText;
            };
        };

        var equipListMaxGridColModel;
        var equipListMaxGridOn = false;
        var equipListMaxGrid;
        var equipListMaxGridStore;
        var preEquipModelId=0;
        function getEquipListMaxGrid(deviceModelId) {

            var deviceModelIds = Number(deviceModelId);

            var width = $("#EquipListGridDiv").width();
            equipListMaxGridStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/device/operationLog/getEquipmentGridData.do",
                baseParams:{
                    deviceModelId  : deviceModelIds
                },
                totalProperty: 'total',
                root:'gridDatas',
                 fields: [
                { name: 'id', type: 'Integer' },
                { name: 'no', type: 'Integer' },
                { name: 'level', type: 'Integer' },
                { name: 'modelId', type: 'Integer' },
                { name: 'desc', type: 'String' },
                { name: 'operation', type: 'String' }
                ]
            });

            var editable = true;
            if (cmdAuth != "true") {
                editable = false;
            }

            equipListMaxGridColModel = new Ext.grid.ColumnModel({
                columns: [

                    {header: "No",
                       dataIndex: 'no',
                       width: 80
                     }
                     ,{header: fmtMessage[1],
                       dataIndex: 'operation',
                       align:'center',
                       width: 150
                     }
                    ,{header: fmtMessage[11],
                        width: 185,
                        dataIndex: 'desc',
                        align: 'center'
                     }
                     ,{header: "Level",
                        width: 185,
                        dataIndex: 'level',
                        align: 'center',
                        editable: editable,
                        editor: combo,
                        renderer: Ext.util.Format.comboRenderer(combo)
                     }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-50)/4)-chromeColAdd

                },

            });
            if (equipListMaxGridOn == false) {
                equipListMaxGrid  = new Ext.grid.EditorGridPanel({
                    id: 'EquipListGrid',
                    store: equipListMaxGridStore,
                    colModel : equipListMaxGridColModel,
                    autoScroll: false,
                    width: width,
                    height: 500,
                    stripeRows : true,
                    columnLines: true,
                    clicksToEdit : 1,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'EquipListGridDiv',
                    viewConfig: {
                        forceFit:true,
                        scrollOffset: 0,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 20,
                        store: equipListMaxGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                equipListMaxGridOn  = true;
            } else {
                equipListMaxGrid.setWidth(width);
                equipListMaxGrid.reconfigure(equipListMaxGridStore, equipListMaxGridColModel);
                var bottomToolbar = equipListMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(equipListMaxGridStore);
            }
            preEquipModelId=deviceModelId;
        };

        var operationListMaxGridColModel;
        var operationListMaxGridOn = false;
        var operationListMaxGrid;
        var operationListMaxGridStore;
        function getOperationListMaxGrid(operationCodeId) {

            var operationCodeIds = Number(operationCodeId);

            var width = $("#OperationListGridDiv").width();
            operationListMaxGridStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/device/operationLog/getOperationGridData.do",
                baseParams:{
                    operationCodeId  : operationCodeIds
                },
                totalProperty: 'total',
                root:'gridDatas',
                 fields: [
                { name: 'id', type: 'Integer' },
                { name: 'no', type: 'Integer' },
                { name: 'level', type: 'Integer' },
                { name: 'modelId', type: 'Integer' },
                { name: 'desc', type: 'String' },
                { name: 'equipment', type: 'String' }
                ]
            });

            operationListMaxGridColModel = new Ext.grid.ColumnModel({
                columns: [

                    {header: "No",
                       dataIndex: 'no',
                       width: 80
                     }
                     ,{header: fmtMessage[14],
                       dataIndex: 'equipment',
                       align:'center',
                       width: 335
                     }
                     ,{header: "Level",
                        width: 185,
                        dataIndex: 'level',
                        align: 'center',
                        editor: combo,
                        renderer: Ext.util.Format.comboRenderer(combo)
                     }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-50)/4)-chromeColAdd

                },

            });
            if (operationListMaxGridOn == false) {
                operationListMaxGrid  = new Ext.grid.EditorGridPanel({
                    id: 'OperationListGrid',
                    store: operationListMaxGridStore,
                    colModel : operationListMaxGridColModel,
                    autoScroll: false,
                    width: width,
                    height: 500,
                    stripeRows : true,
                    columnLines: true,
                    clicksToEdit : 1,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'OperationListGridDiv',
                    viewConfig: {
                        forceFit:true,
                        scrollOffset: 0,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 20,
                        store: operationListMaxGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                operationListMaxGridOn  = true;
            } else {
                operationListMaxGrid.setWidth(width);
                operationListMaxGrid.reconfigure(operationListMaxGridStore, operationListMaxGridColModel);
                var bottomToolbar = operationListMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(operationListMaxGridStore);
            }
        };

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
    </script>
</head>

<body>


<!-- DIV 1 : searchDiv (S) -->
<div id="searchDiv">
    <div id="btn" class="btn_topright">
        <ul>
            <li><a href="javascript:changeDiv('settingDiv');"><span
                class="greenbold11pt"><fmt:message key="aimir.setting"/></span></a></li>
        </ul>
    </div>

    <!-- search-background DIV (S) -->
    <div class="search-bg-withtabs">
        <div class="dayoptions">
            <%@ include file="../commonDateTab.jsp"%>
        </div>
        <div class="dashedline">
            <ul>
                <li></li>
            </ul>
        </div>
        <div class="searchoption-container">
            <table class="searchoption wfree">
                <tr>
                    <td class="gray11pt withinput"><fmt:message key="aimir.operatorType" /></td>
                    <td><select id="operatorType" name="operatorType"
                        style="width: 160px">
                        <option value=""><fmt:message key="aimir.all" /></option>
                        <option value="1"><fmt:message key="aimir.operator" /></option>
                        <option value="0"><fmt:message key="aimir.system" /></option>
                    </select></td>
                    <td class="space20"></td>

                    <td class="gray11pt withinput"><fmt:message key="aimir.operator" /></td>
                    <td><input id="userId" name="userId" type="text" /></td>
                    <td class="space20"></td>

                    <td class="gray11pt withinput"><fmt:message key="aimir.targetType" /></td>
                    <td><select id="targetType" name="targetType" style="width: 90px">
                        <option value=""><fmt:message key="aimir.all" /></option>
                        <c:forEach var="target" items="${targets}">
                            <option value="${target.id}">${target.descr}</option>
                        </c:forEach>
                    </select></td>
                    <td class="space20"></td>

                    <td class="gray11pt withinput"><fmt:message key="aimir.target" /></td>
                    <td><input id="targetName" name="targetName" type="text" /></td>
                </tr>

                <tr>
                    <td class="gray11pt withinput"><fmt:message key="aimir.status" /></td>
                    <td><select id="status" name="status" style="width: 160px">
                        <!-- 0:Success, 1:Failed, 2:Invalid Argument, 3:Communication Failure -->
                        <option value=""><fmt:message key="aimir.all" /></option>
                        <option value="1"><fmt:message key="aimir.failed" /></option>
                        <option value="0"><fmt:message key="aimir.success" /></option>
                    </select></td>
                    <td class="space20"></td>
<!--
                    <td class="gray11pt withinput"><fmt:message key="aimir.description" /></td>
                    <td colspan="7"><input id="description" name="description"
                        type="text" style="width: 512px;" /></td> -->

                    <td>
                    <div id="btn">
                    <ul>
                        <li><a href="javascript:searchList();" class="on"><fmt:message
                            key="aimir.button.search" /></a></li>
                    </ul>
                    </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>
    <!-- search-background DIV (E) -->
    <div class="gadget_body">
        <div class="floatleft" style="width: 49%;">
            <div id="fcChartDiv" style="height:290px;">The chart will appear within this DIV. This text will be replaced by the chart.</div>
        </div>
        <div id="operationLogMaxGridDiv" class="floatright grid-button-height" style="width: 50%;"></div>
        <div id="btn" class="btn_right_top2 margin-t10px">
    <ul><li><a href="javascript:openExcelReport()" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
</div>
         <div id="operationLogDetailGridDiv" class="clear padding-t10px"></div>

    </div>

</div>

<!-- DIV 1 : searchDiv (E) --> <!-- DIV 2 : settingDiv (S) -->
<div id="settingDiv" style="display: none">
    <div id="btn" class="btn_topright">
        <ul>
            <li><a href="javascript:changeDiv('searchDiv');"><span
                class="greenbold11pt"><fmt:message key="aimir.backToSearch" /></span></a></li>
        </ul>
    </div>
    <div id="gad_sub_tab"></div>

    <!-- Gadget Body (S) -->
    <div class="gadget_body">
        <div class="searchoption select-treetype">
            <ul>

                <li><input name="rdBtn" class="radio" type="radio"
                    onclick="changeTreeDiv('equipmentDiv')" checked /></li>
                <li class="blue11pt withinput"><fmt:message key="aimir.equipment" /></li> <!-- jhkim 메시지화 -->
                <li><input name="rdBtn" class="radio" type="radio"
                    onclick="changeTreeDiv('operationDiv')" /></li>
                <li class="blue11pt withinput"><fmt:message key="aimir.operation" /></li> <!-- jhkim 메시지화 -->
            </ul>
        </div>

        <div class="bodyright_operationlogsetting">
            <ul>
                <li class="bodyright_operationlogsetting_leftmargin">
                    <div id="EquipListGridDiv"></div>
                    <div id="OperationListGridDiv"></div>
                    <div class="btn_right_bottom">

                        <div id="saveBtnList" class="floatright">
                            <em class="am_button margin-r5">
                                <a href="javascript:updateOperationList();"><fmt:message key="aimir.save2" /></a>
                            </em>
                            <em class="am_button">
                                <a href="javascript:cancelOperationList();"><fmt:message key="aimir.cancel" /></a>
                            </em>
                        </div>
                    </div>
                </li>
            </ul>
        </div>

        <div class="clear-width100"></div>
        <div class="bodyleft_operationlogsetting"><!--  Tree (S) -->
            <div class="foldertree_border">
                <fieldset class="foldertree">
                    <div id="equipmentDiv"></div>
                    <div id="operationDiv" style="display: none"></div>
                </fieldset>
            </div>
        <!-- 제조사/장비별 Tree (E) -->
        </div>

    </div>
<!-- Gadget Body (E) -->

</div>
<!-- DIV 2 : settingDiv (E) -->
</body>
</html>