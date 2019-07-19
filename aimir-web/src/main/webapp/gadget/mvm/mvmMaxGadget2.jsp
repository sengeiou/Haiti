<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* grid 안에 button 이 있는 경우 높이조정 */
        .x-grid3-cell-inner {
            padding-top: 0px;
            padding-bottom: 0px;
        }
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/sic.tree.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    
    <%-- Ext-JS 관련 --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <%-- TreeGrid 관련 js --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        var tabType = "hour";
        var allStr = "<fmt:message key="aimir.all"/>";
        defaultSelect = DateType.DAILY;

        //공급사ID
        var supplierId="${supplierId}";
        var supplierName = "";
        // user location
        var permitLocationId = "${permitLocationId}";

        var d = new Date();
        var mvmMiniType = '${mvmMiniType}';

        var chromeColAdd = 0;
        // Chrome 최선버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        function extColumnResize() {
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
        }

        Ext.onReady(function() {
            Ext.QuickTips.init();
            extColumnResize();

            getMeteringDataList();
        });

        $(function() {
            /**
             * 유저 세션 정보 가져오기
             */
            $.getJSON('${ctx}/common/getUserInfo.do',
                    function(json) {
                        if (json.supplierId != "") {
                            supplierName = json.supplierName;
                            //계약그룹 selectBox 초기화
                            getContractGroup();
                        }
                    }
            );

            // Dialog
            $('#detail_dialog').dialog({
                autoOpen: false,
                resizable: false,
                //height: 800,
                //width: 1350,
                //iframe:true,
                modal: false
            });

            // Dialog Link
            $('#dialog_link2').click(function() {
                $('#detail_dialog').dialog('open');
                return false;
            });

            // Dialog
            $('#chart_dialog').dialog({
                autoOpen: false,
                resizable: false,
                //height: 1550,
                //width: 1350,
                //iframe:true,
                modal: false
            });
            // Dialog Link
            $('#dialog_link1').click(function() {
                $('#chart_dialog').dialog('open');
                return false;
            });

            $('#contractGourp').selectbox();
            $('#meteringSF').selectbox();
            $('#customer_type').selectbox();
            $('#device_type').selectbox();

            if (permitLocationId != null && permitLocationId != "") {
                locationTreeForPermitLocation('treeeDiv', 'searchWord', 'location', permitLocationId);
            } else {
                locationTreeGoGo('treeeDiv', 'searchWord', 'location');
            }
            sicTreeGoGo('tree2Div', 'searchWord2', 'customType', null, 'sicIds');   // sicIds:상위노드 포함 하위노드가 저장되는 field
            
        });

        function clearSearchItem() {
            document.getElementById("customer_number").value = "";
            document.getElementById("customer_name").value = "";
            document.getElementById("meteringSF").value = "s";
            document.getElementById("location").value = "";
            document.getElementById("customer_type").value = "";
            document.getElementById("mcu_id").value = "";
            document.getElementById("meter_id").value = "";
            document.getElementById("mdev_id").value = "";
        }

        function showChartOpen(contractNos, meterListObj, meterList) {
            var stdDate = "";
            var endDate = "";
            var comboValue ="";
            var dateType = new Array("hour","day","","week","month","","dayWeek","season","year");
            tabType = $('#searchDateType').val();

            if ("day" == dateType[tabType]) {
                comboValue = $('#periodType').val();
            } else if ("dayWeek" == dateType[tabType]) {
                comboValue = $('#weeklyWeekCombo').val();
            } else {
                comboValue = 0; // 빼면 에러남/ 초기화 시킴
            }

            if ($('#searchDateType').val() == 0) {
                stdDate = $('#searchStartDate').val()+$('#searchStartHour').val();
                endDate = $('#searchEndDate').val()+ $('#searchEndHour').val();
            } else {
                stdDate =  $('#searchStartDate').val();
                endDate = $('#searchEndDate').val();
            }

            var meterNos = new Array();
            for (var attr in meterListObj) {
                meterNos.push(meterListObj[attr].join(","));
            }
            var path = "${ctx}/gadget/mvm/mvmChartView.do";
            var params = {contractNos : contractNos.join(","),
                    meterList : meterList.join(","),
                    meterNos :  meterNos.join(","),
                    mvmMiniType : "${mvmMiniType}",
                    tabType : tabType,
                    stdDate : stdDate,
                    endDate : endDate,
                    comboValue : comboValue == undefined ? "undefined" : comboValue};
            $('#chart_dialog').dialog('open');
            sendChartPostMethod(path,params,"post","chart_view");
        }

        function sendChartPostMethod(path, params, method, target) {        	
            $("#chartForm input[name=contractNos]").val(params.contractNos);
			$("#chartForm input[name=meterList]").val(params.meterList);
			$("#chartForm input[name=meterNos]").val(params.meterNos);
			$("#chartForm input[name=mvmMiniType]").val(params.mvmMiniType);
			$("#chartForm input[name=tabType]").val(params.tabType);
			$("#chartForm input[name=stdDate]").val(params.stdDate);
			$("#chartForm input[name=endDate]").val(params.endDate);
			$("#chartForm input[name=comboValue]").val(params.comboValue);

            var $chartForm = document.getElementById("chartForm");
            $chartForm.action=path;

            $chartForm.submit();
        }
        
        function showDetail(meterNo, contractId) {
        	
            var stdDate = "";
            var endDate = "";
            var comboValue ="";
            var dayComboValue ="";
            var dateType = new Array("hour","day","","week","month","","dayWeek","season","year");
            tabType = $('#searchDateType').val();

            if ("day" == dateType[tabType]) {
                comboValue = $('#periodType').val();
                dayComboValue = 0;
            }
            else if ("week" == dateType[tabType]) {
                comboValue = $('#weeklyWeekCombo').val();
                dayComboValue = 0;
            }
            else if ("dayWeek" == dateType[tabType]) {
                comboValue = $('#weekDailyWeekCombo').val();
                dayComboValue = $('#weekDailyWeekDayCombo').val();
            }
            else if ("season" == dateType[tabType]) {
                comboValue = $('#seasonalSeasonCombo').val();
                dayComboValue = 0;
            }else if ("year" == dateType[tabType]) {
                comboValue = $('#seasonalSeasonCombo').val();
                dayComboValue = 0;
            } else {
                comboValue = 0; // 빼면 에러남/ 초기화 시킴
                dayComboValue = 0;
            }
            stdDate =  $('#searchStartDate').val();
            endDate = $('#searchEndDate').val();
            
            var path = "${ctx}/gadget/mvm/mvmDetailView2.do";
            var params = {meterNo : meterNo,
                    contractId : contractId,
                    mvmMiniType : "${mvmMiniType}",
                    tabType : tabType,
                    stdDate : stdDate,
                    endDate : endDate,
                    comboValue : comboValue,
                    dayComboValue : dayComboValue,
                    supplierId : supplierId
                };
            
            
            
			if(stdDate != ''){
				$('#detail_dialog').dialog('open');
				sendDetailPostMethod(path, params, "post", "detail_view");
				
				
            } else {
            	Ext.Msg.alert("<fmt:message key='aimir.message'/>", '<div style="width: 150px"><fmt:message key="aimir.season.select.error"/></div>');
            }
        }
        
        function sendDetailPostMethod(path, params, method, target) {
            $("#detailForm input[name=meterNo]").val(params.meterNo);
			$("#detailForm input[name=contractId]").val(params.contractId);
			$("#detailForm input[name=mvmMiniType]").val(params.mvmMiniType);
			$("#detailForm input[name=tabType]").val(params.tabType);
			$("#detailForm input[name=stdDate]").val(params.stdDate);
			$("#detailForm input[name=endDate]").val(params.endDate);
			$("#detailForm input[name=comboValue]").val(params.comboValue);
			$("#detailForm input[name=dayComboValue]").val(params.dayComboValue);
			$("#detailForm input[name=supplierId]").val(params.supplierId);            
			
            var $detailForm = document.getElementById("detailForm");
            $detailForm.action=path;

            $detailForm.submit();
        }

        //Contract Group 초기화.
        function getContractGroup() {
           $.getJSON('${ctx}/gadget/system/getContractGroup.do'
                    , {'supplierId' : supplierId}
                    , function (returnData) {
                        $('#contractGroup').loadSelect(returnData.NAME);
                        $('#contractGroup').selectbox();
                    });
           };

        function getFmtMessage() {
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.contractNumber"/>";
            fmtMessage[2] = "<fmt:message key="aimir.customername"/>";
            fmtMessage[3] = "<fmt:message key="aimir.meteringtime"/>";
            fmtMessage[4] = "<fmt:message key="aimir.usage"/>";
            fmtMessage[5] = "<fmt:message key="aimir.previous"/>";
            fmtMessage[6] = "<fmt:message key="aimir.co2formula"/>";
            fmtMessage[7] = "<fmt:message key="aimir.mcuid2"/>";
            fmtMessage[8] = "<fmt:message key="aimir.meterid2"/>";
            fmtMessage[9] = "<fmt:message key="aimir.location"/>";
            fmtMessage[10] = "<fmt:message key="aimir.view.detail"/>";
            fmtMessage[11] = "<fmt:message key="aimir.alert"/>";
            fmtMessage[12] = "<fmt:message key="aimir.comparison.cannot.use"/>";
            fmtMessage[13] = "<fmt:message key="aimir.firmware.msg09"/>";
            fmtMessage[14] = "<fmt:message key="aimir.alert.metering.choiceContract4"/>";   // 선택한 계약이 4개를 초과했습니다.
            fmtMessage[15] = "<fmt:message key="aimir.excel.meteringDataList"/>";

            return fmtMessage;
        }

        // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
        var tabs = {hourly:1,daily:1,period:0,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:1,seasonal:1,yearly:1};

        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'', daily:'', period:'', weekly:'', monthly:'', monthlyPeriod:'', weekDaily:'', seasonal:'', yearly:''};

        var inputDate = new Object();
        inputDate._dateType = DateType.DAILY;  // 최초 조회탭
        inputDate.dailyStartDate = "${formatDate}";
        inputDate.searchStartDate = "${currentDate}";
        inputDate.searchEndDate   = "${currentDate}";
        tabs.InputDate = inputDate;

        // 공통조회화면 필수 function
        function send() {
            getMeteringDataList();
        }

        //report window(Excel)
        var winObj;
        function openExcelReport() {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            obj.searchDateType = $('#searchDateType').val();
            obj.searchStartDate = $('#searchStartDate').val();
            obj.searchStartHour = $('#searchStartHour').val();
            obj.searchEndDate = $('#searchEndDate').val();
            obj.searchEndHour = $('#searchEndHour').val();
            obj.searchWeek = $("#weeklyWeekCombo").val();

            obj.customer_number = document.getElementById("customer_number").value;
            obj.customer_name = document.getElementById("customer_name").value;
            obj.meteringSF = document.getElementById("meteringSF").value;
            obj.location        = document.getElementById("location").value;
            obj.permitLocationId = permitLocationId;
            obj.customer_type   = document.getElementById("customer_type").value;
            obj.mcu_id          = document.getElementById("mcu_id").value;
            obj.meter_id        = document.getElementById("meter_id").value;
            obj.device_type     = document.getElementById("device_type").value;
            obj.mdev_id         = document.getElementById("mdev_id").value;
            obj.contractGroup   = document.getElementById("contractGroup").value;
            obj.customType      = $("#customType").val();
            obj.mvmMiniType     = "${mvmMiniType}";
            obj.supplierId      = supplierId;
            obj.title               = "<fmt:message key='aimir.excel.meteringData'/>";

            if(winObj)
                winObj.close();
            winObj = window.open("${ctx}/gadget/mvm/mvmMaxExcelDownloadPopup.do", "MeteringDataExcel", opts);
            winObj.opener.obj = obj;
        }
        
        /* Metering Data 리스트 START */
        var meteringDataGridOn = false;
        var meteringDataGrid;
        var meteringDataColModel;
        var meteringDataCheckSelModel;
        
        var getMeteringDataList = function() {
            var width = $("#meteringDataGridDiv").width();
            var pageSize = 15;
            var meteringDataStore = new Ext.data.JsonStore({
                autoLoad : {params:{start: 0, limit: pageSize}},
                url : "${ctx}/gadget/mvm/getMeteringDataList.do",
                baseParams : {
                    supplierId : supplierId,
                    contractNumber : $("#customer_number").val(),
                    customerName : $("#customer_name").val(),
                    meteringSF : $("#meteringSF").val(),
                    searchDateType : $("#searchDateType").val(),
                    searchStartDate : $("#searchStartDate").val(),
                    searchStartHour : $("#searchStartHour").val(),
                    searchEndDate : $("#searchEndDate").val(),
                    searchEndHour : $("#searchEndHour").val(),
                    searchWeek : $("#weeklyWeekCombo").val(),
                    locationId : $("#location").val(),
                    permitLocationId : permitLocationId,
                    tariffType : $("#customer_type").val(),
                    mcuId : $("#mcu_id").val(),
                    deviceType : $("#device_type").val(),
                    mdevId : $("#mdev_id").val(),
                    contractGroup : $("#contractGroup").val(),
                    sicId : $("#customType").val(),
                    sicIds : $("#sicIds").val(),
                    mvmMiniType : '${mvmMiniType}'
                },
                totalProperty : 'totalCount',
                root : 'result',
                fields : ["num", "contractNumber", "customerName", "meteringTime", "value", "prevValue", "meterNo", "modemId"],
                listeners : {
                    beforeload : function(store, options) {
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page : Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    }
                }
            });
            
            if (meteringDataGridOn == false) {
                meteringDataCheckSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly : true
                    ,dataIndex : 'meterId'
                });
            }
            
            var colWidth = (width-70)/8;

            	if (mvmMiniType.toString() == "EM") {
                    if(supplierName == 'MOE'){    // 추후 이라크 선불 도입시 코드 원래대로 수정할것.
                        // 그전에 검색창도 contract 관련 항목 disable 시킨다
                        $('#customer_number').attr('readOnly','readonly');
                        $('#customer_name').attr('readonly','readonly');
                        $('#searchWord2').attr('readonly','readonly');

                        meteringDataColModel = new Ext.grid.ColumnModel({
                            columns : [
                                  meteringDataCheckSelModel
                                 ,{header: "<fmt:message key='aimir.number'/>", dataIndex: 'num', width: 50}
                                 ,{header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber', hidden:true}
                                 ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName', hidden:true}
                                 ,{header: "<fmt:message key='aimir.meteringtime'/>", dataIndex: 'meteringTime'}
                                 ,{header: "<fmt:message key='aimir.usage.kwh'/>", dataIndex: 'value', align: 'right'}
                                 ,{header: "<fmt:message key='aimir.previous'/>[<fmt:message key='aimir.unit.kwh'/>]", dataIndex: 'prevValue', align: 'right'}
                                 ,{header: "<fmt:message key='aimir.meterid2'/>", dataIndex: 'meterNo'}
                                 ,{header: "<fmt:message key='aimir.modemid'/>", dataIndex: 'modemId'}
                                 ,{header: "<fmt:message key='aimir.view.detail'/>",
                                     renderer: function(value, metaData, record, index) {
                                         var btnHtml = "<a href='#;' onclick='showDetail(\"{meterNo}\", \"{contractNumber}\");' class='btn_blue'><span><fmt:message key="aimir.view.detail"/></span></a>";
                                         var tplBtn = new Ext.Template(btnHtml);
                                         return tplBtn.apply({meterNo: record.get('meterNo'), contractNumber: record.get('contractNumber')});
                                     }
                                 }
                            ],
                            defaults : {
                                sortable : true
                               ,menuDisabled : true
                               ,width : colWidth
                               ,align : 'center'
                            }
                        });
                    }else{
                        meteringDataColModel = new Ext.grid.ColumnModel({
                            columns : [
                                  meteringDataCheckSelModel
                                 ,{header: "<fmt:message key='aimir.number'/>", dataIndex: 'num', width: 50}
                                 ,{header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'}
                                 ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName' }
                                 ,{header: "<fmt:message key='aimir.meteringtime'/>", dataIndex: 'meteringTime'}
                                 ,{header: "<fmt:message key='aimir.usage.kwh'/>", dataIndex: 'value', align: 'right'}
                                 ,{header: "<fmt:message key='aimir.previous'/>[<fmt:message key='aimir.unit.kwh'/>]", dataIndex: 'prevValue', align: 'right'}
                                 ,{header: "<fmt:message key='aimir.meterid2'/>", dataIndex: 'meterNo'}
                                 ,{header: "<fmt:message key='aimir.modemid'/>", dataIndex: 'modemId'}
                                 ,{header: "<fmt:message key='aimir.view.detail'/>",
                                     renderer: function(value, metaData, record, index) {
                                         var btnHtml = "<a href='#;' onclick='showDetail(\"{meterNo}\", \"{contractNumber}\");' class='btn_blue'><span><fmt:message key="aimir.view.detail"/></span></a>";
                                         var tplBtn = new Ext.Template(btnHtml);
                                         return tplBtn.apply({meterNo: record.get('meterNo'), contractNumber: record.get('contractNumber')});
                                     }
                                 }
                            ],
                            defaults : {
                                sortable : true
                               ,menuDisabled : true
                               ,width : colWidth
                               ,align : 'center'
                            }
                        });                        
                    }


                }
            
            else if (mvmMiniType.toString() == "GM" || mvmMiniType.toString() == "WM") {
                meteringDataColModel = new Ext.grid.ColumnModel({
                    columns : [
                          meteringDataCheckSelModel
                         ,{header: "<fmt:message key='aimir.number'/>", dataIndex: 'num', width: 50}
                         ,{header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'}
                         ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName'}
                         ,{header: "<fmt:message key='aimir.meteringtime'/>", dataIndex: 'meteringTime'}
                         ,{header: "<fmt:message key='aimir.usage.m3'/>", dataIndex: 'value', align: 'right'}
                         ,{header: "<fmt:message key='aimir.previous'/>[<fmt:message key='aimir.unit.m3'/>]", dataIndex: 'prevValue', align: 'right'}
                         ,{header: "<fmt:message key='aimir.meterid2'/>", dataIndex: 'meterNo'}
                         ,{header: "<fmt:message key='aimir.modemid'/>", dataIndex: 'modemId'}
                         ,{header: "<fmt:message key='aimir.view.detail'/>",
                             renderer: function(value, metaData, record, index) {
                                           var btnHtml = "<div><a href='#;' onclick='showDetail(\"{meterNo}\", \"{contractNumber}\");' class='btn_blue'><span><fmt:message key="aimir.view.detail"/></span></a></div>";
                                           var tplBtn = new Ext.Template(btnHtml);
                                           return tplBtn.apply({meterNo: record.get('meterNo'), contractNumber: record.get('contractNumber')});
                             }
                         }
                    ],
                    defaults : {
                        sortable : true
                       ,menuDisabled : true
                       ,width : colWidth
                       ,align : 'center'
                    }
                });

            } else if (mvmMiniType.toString() == "HM") {
                meteringDataColModel = new Ext.grid.ColumnModel({
                    columns : [
                          meteringDataCheckSelModel
                         ,{header: "<fmt:message key='aimir.number'/>", dataIndex: 'num', width: 50}
                         ,{header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'}
                         ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName'}
                         ,{header: "<fmt:message key='aimir.meteringtime'/>", dataIndex: 'meteringTime'}
                         ,{header: "<fmt:message key='aimir.usage'/>[<fmt:message key='aimir.unit.Gcal'/>]", dataIndex: 'value', align: 'right'}
                         ,{header: "<fmt:message key='aimir.previous'/>[<fmt:message key='aimir.unit.Gcal'/>]", dataIndex: 'prevValue', align: 'right'}
                         ,{header: "<fmt:message key='aimir.meterid2'/>", dataIndex: 'meterNo'}
                         ,{header: "<fmt:message key='aimir.modemid'/>", dataIndex: 'modemId'}
                         ,{header: "<fmt:message key='aimir.view.detail'/>",
                             renderer: function(value, metaData, record, index) {
                                           var btnHtml = "<a href='#;' onclick='showDetail(\"{meterNo}\", \"{contractNumber}\");' class='btn_blue'><span><fmt:message key="aimir.view.detail"/></span></a>";
                                           var tplBtn = new Ext.Template(btnHtml);
                                           return tplBtn.apply({meterNo: record.get('meterNo'), contractNumber: record.get('contractNumber')});
                             }
                         }
                    ],
                    defaults : {
                        sortable : true
                       ,menuDisabled : true
                       ,width : colWidth
                       ,align : 'center'
                    }
                });

            } 
            else {
                meteringDataColModel = new Ext.grid.ColumnModel({
                    columns : [
                          meteringDataCheckSelModel
                         ,{header: "<fmt:message key='aimir.number'/>", dataIndex: 'num', width: 50}
                         ,{header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'contractNumber'}
                         ,{header: "<fmt:message key='aimir.customername'/>", dataIndex: 'customerName'}
                         ,{header: "<fmt:message key='aimir.meteringtime'/>", dataIndex: 'meteringTime'}
                         ,{header: "<fmt:message key='aimir.usage'/>", dataIndex: 'value', align: 'right'}
                         ,{header: "<fmt:message key='aimir.previous'/>", dataIndex: 'prevValue', align: 'right'}
                         ,{header: "<fmt:message key='aimir.meterid2'/>", dataIndex: 'meterNo'}
                         ,{header: "<fmt:message key='aimir.modemid'/>", dataIndex: 'modemId'}
                         ,{header: "<fmt:message key='aimir.view.detail'/>",
                             renderer: function(value, metaData, record, index) {
                            		 var btnHtml = "<a href='#;' onclick='showDetail(\"{meterNo}\", \"{contractNumber}\");' class='btn_blue'><span><fmt:message key="aimir.view.detail"/></span></a>";
                                     var tplBtn = new Ext.Template(btnHtml);
                            		 return tplBtn.apply({meterNo: record.get('meterNo'), contractNumber: record.get('contractNumber')});
                             }
                         }
                    ],
                    defaults : {
                        sortable : true
                       ,menuDisabled : true
                       ,width : colWidth
                       ,align : 'center'
                    }
                });
            }
            	
                if("WM" == '${mvmMiniType}' || "GM" == '${mvmMiniType}' ){
            		if($('#searchDateType').val() == 0 || $('#searchDateType').val() == 4) {
            			meteringDataColModel.setColumnHeader(5,"<fmt:message key='aimir.meter.value'/>[<fmt:message key='aimir.unit.m3'/>]");
            		} else {
            			meteringDataColModel.setColumnHeader(5,"<fmt:message key='aimir.usage.m3'/>");
            		}
            	}
			
            if (meteringDataGridOn == false) {
                meteringDataGrid = new Ext.grid.GridPanel({
                    store : meteringDataStore,
                    colModel : meteringDataColModel,
                    sm : meteringDataCheckSelModel,
                    width : width,
                    height : 437,
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'meteringDataGridDiv',
                    viewConfig : {
                        forceFit: true,
                        scrollOffset: 0,
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar : new Ext.PagingToolbar({
                        pageSize : pageSize,
                        store : meteringDataStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'
                    })
                });
                meteringDataGridOn = true;
            } else {
                meteringDataGrid.setWidth(width);
                var bottomToolbar = meteringDataGrid.getBottomToolbar();
                meteringDataGrid.reconfigure(meteringDataStore, meteringDataColModel);
                bottomToolbar.bindStore(meteringDataStore);
            } 
            hide();
        };

        // window resize event
        $(window).resize(function() {
            getMeteringDataList();
        });

        function showCmpChart() {
            var contractList = new Array();
            var meterList = new Array();
            var checkedArr = meteringDataCheckSelModel.getSelections();

            if (checkedArr.length > 0) {
                for (var i = 0 ; i < checkedArr.length ; i++) {
                    if (checkedArr[i].get("contractNumber") != null && checkedArr[i].get("contractNumber") != "") {
                        contractList.push(checkedArr[i].get("contractNumber"));
                        meterList.push(checkedArr[i].get("meterNo"));
                    }
                }
            }

            var len = contractList.length;
            var meterListObj = new Object();

            for (i = 0 ; i < len ; i++) {
                if (meterListObj[contractList[i]] != null) {
                    meterListObj[contractList[i]].push(meterList[i]);
                } else {
                    meterListObj[contractList[i]] = new Array();
                    meterListObj[contractList[i]].push(meterList[i]);
                }
            }

            contractList.sort();

            var fmtMessage = getFmtMessage();
            var dataCount = contractList.length;
            var cnt = 1;

            for (i = 0 ; i < dataCount ; i++) {
                if (i < (dataCount-1)) {
                    if (contractList[i] != contractList[(i+1)]) {
                        cnt++;
                        if (cnt > 4) {
                            Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', fmtMessage[14]);
                            return;
                        }
                    }
                }
            }

            if (contractList.length > 0) {
                showChartOpen(contractList, meterListObj, meterList);
            } else {
                if (meteringDataGrid.getStore().getCount() <= 0) {
                    Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', "Please search for data.");
                } else {
                    Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', fmtMessage[12]);
                }
            }
        }
    /*]]>*/
    </script>
</head>
<body onLoad="javascript:hide()">
<input type="hidden" id="sicIds" name="sicIds"/>

<!-- search-background DIV (S) -->
<div class="search-bg-withtabs">
    <div class="dayoptions">
    <%@ include file="/gadget/commonDateTab.jsp"%>
    </div>
    <div class="dashedline"><ul><li></li></ul></div>


    <!--검색조건-->
    <div class="searchoption-container">
        <table class="searchoption wfree" >
            <tr>
            <!-- Contrat Group -->
                <td class="gray11pt withinput" style="width: 90px"><fmt:message key="aimir.contractgroup"/></td>
                <td>
                    <select id="contractGroup" name="select" style="width:120px;">
                        <option value=""><fmt:message key="aimir.all"/></option>
                    </select>
                </td>
                <td class="space20"></td>
                <td class="gray11pt withinput" style="width:80px;"><fmt:message key="aimir.contractNumber"/></td>
                <td><input id="customer_number" type="text" style="width:110px;"></td>
                <td class="space20"></td>
                <td class="gray11pt withinput" style="width: 95px"><fmt:message key="aimir.customername"/></td>
                <td><input id="customer_name" type="text" style="width:120px;"></td>
                <td class="space20"></td>
                <td class="gray11pt withinput" style="width:40px"><fmt:message key="aimir.sic" /><!-- 산업분류코드 --></td>
                <td class="space20">
                    <input name="searchWord2" id="searchWord2" style="width:120px;" type="text" />
                    <input type="hidden" id="customType" value=""></input>
                </td>
                <td class="space20"></td>
                <td class="gray11pt withinput"><fmt:message key="aimir.button.metering"/></td>
                <td>
                    <select id="meteringSF" style="width:80px">
                        <option value="s"><fmt:message key="aimir.success"/></option>
                        <option value="f"><fmt:message key="aimir.failed"/></option>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="gray11pt withinput" style="width: 95px"><fmt:message key="aimir.location"/></td>
                <td>
                    <input name="searchWord" id='searchWord' style="width:120px;" type="text" />
                    <input type="hidden" id="location" value=""></input>
                </td>
                <td class="space20"></td>
                <td class="gray11pt withinput" style="width: 85px"><fmt:message key="aimir.deviceType"/></td>
                <td><form:select id="device_type"  path="deviceType" items="${deviceType}" style="width:110px;"/></td>
                <td class="space20"></td>
                <td class="gray11pt withinput" style="width: 110px"><fmt:message key="aimir.meterid2"/></td>
                <input id="meter_id" type="text" style="width:120px;display: none">
                <td><input id="mdev_id" type="text" style="width:120px;"></td>
                <td class="space20"></td>
                <td class="gray11pt withinput" style="width:90px"><fmt:message key="aimir.mcuid"/></td>
                <td><input id="mcu_id" type="text" style="width:120px;"></td>
                <td class="space20"></td>
                <td class="gray11pt withinput" style="width:60px;"><fmt:message key="aimir.contract.tariff.type"/></td>
                <td><form:select id="customer_type"  path="tariffType" items="${tariffType}" style="width:230px;"/></td>
            </tr>
        </table>
        <div class="clear">
            <div id="tree2DivOuter" class="tree-billing auto"  style="display:none;">
                <div id="tree2Div"></div>
            </div>
        </div>

        <div class="clear">
            <div id="treeeDivOuter" class="tree-billing auto"  style="display:none;">
                <div id="treeeDiv"></div>
            </div>
        </div>
    </div> 
    <!--검색조건 끝-->

</div>
<!-- search-background DIV (E) -->

<div id="btn" class="btn_right_top2 margin-t10px">
    <ul><li><a href="javascript:showCmpChart()" class="on"><fmt:message key="aimir.comparison"/>&nbsp;<fmt:message key="aimir.view.chart"/></a></li></ul>
    <ul><li><a href="javascript:openExcelReport()" class="on"><fmt:message key="aimir.button.excel"/></a></li></ul>
</div>
<div class="gadget_body2">
    <div id="meteringDataGridDiv"></div>
</div>

<div align="left"></div>
<br style="clear:both;" />

<div id="chart_dialog" class="mvm-popwin-iframe-outer" title="<fmt:message key='aimir.view.chart'/>">
    <iframe id="chart_view" name="chart_view" src="" frameborder="0" class="mvm-popwin-iframe" marginwidth="0" marginheight="0" scrolling="no"></iframe>
</div>


<form id='chartForm' name='chartForm' method='post' action='' target='chart_view'>
	<input type='hidden' name='contractNos' value=''>
	<input type='hidden' name='meterList' value=''>
	<input type='hidden' name='meterNos' value=''>
	<input type='hidden' name='mvmMiniType' value=''>
	<input type='hidden' name='tabType' value=''>
	<input type='hidden' name='stdDate' value=''>
	<input type='hidden' name='endDate' value=''>
	<input type='hidden' name='comboValue' value=''>
</form>

<div id="detail_dialog" class="mvm-popwin-iframe-outer" title="<fmt:message key='aimir.view.detail'/>">
    <iframe id="detail_view" name="detail_view" src="" frameborder="0"  class="mvm-popwin-iframe" marginwidth="0" marginheight="0" scrolling="no"></iframe>
</div>
<form id='detailForm' name='detailForm' method='post' action='' target='detail_view'>
	<input type='hidden' name='meterNo' value=''>
	<input type='hidden' name='contractId' value=''>
	<input type='hidden' name='mvmMiniType' value=''>
	<input type='hidden' name='tabType' value=''>
	<input type='hidden' name='stdDate' value=''>
	<input type='hidden' name='endDate' value=''>
	<input type='hidden' name='comboValue' value=''>
	<input type='hidden' name='dayComboValue' value=''>
	<input type='hidden' name='supplierId' value=''>
    <input type='hidden' name='showCO2' value=''>
</form>
</body>
</html>
