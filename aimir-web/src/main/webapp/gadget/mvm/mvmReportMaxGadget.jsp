<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <title>Metering Data Report</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}

        .x-pivotgrid .x-grid3-row-headers table td {
            height: 19px !important;
        }

        .ext-gecko .x-pivotgrid .x-grid3-row-headers table td {
            height: 22px !important;
        }

        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
         /* tree node 의 style 이 어긋나는 부분 수정 */
        .x-tree-node span, .x-tree-node a {
            float:none !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        var tabs = {
            hourly : 0,
            daily : 1,
            period : 0,
            weekly : 1,
            monthly : 1,
            monthlyPeriod : 0,
            weekDaily : 0,
            seasonal : 1,
            yearly : 1
        };
        var tabNames = {};

        var supplierId = ${supplierId};
        var serviceType = ServiceType.Electricity;
        var chromeColAdd = 0;

        // Chrome 최신버전에서 Ext-JS Grid 컬럼사이즈 오류 수정
        Ext.onReady(function() {
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
        });

        $(document).ready(function(){
            Ext.QuickTips.init();
            hide();
            locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
            makeOutputRangeComboBox();
            getTariffTypeList();
            // 
            // 상세 Window 생성
            makeDetailWindow();
            getmvmReportTypeTree();
            getmvmReportGrid();
        });

        //윈도우 리싸이즈시 event
        $(window).resize(function() {
            //리싸이즈시 패널 인스턴스 kill & reload
            mvmReportGrid.destroy();
            mvmReportGridOn = false;

            getmvmReportGrid();
        });

        // 출력범위 combo 생성
        function makeOutputRangeComboBox(){
            var list = new Array();
            var obj = new Object();
            var selectElement = $('#outputRange');
            var optValue = ReportPartition;
            var defaultValue = "";

            $.each(optValue, function(key, value) {
                if(key == "Default") {
                    defaultValue = value;
                } else {
                    obj = new Object();
                    obj.id = value;
                    obj.name = value;
                    list.push(obj);
                }
            });

            if(list.length > 0) {
                $('#outputRange').loadSelect(list);
                //$("#occurFreq option:eq(0)").remove();
                $("#outputRange").val(defaultValue);
                $("#outputRange").selectbox();
            }
        }

        function getTariffTypeList() {
            $.getJSON('${ctx}/gadget/mvm/getTariffTypes.do', {serviceType:serviceType, supplierId:supplierId},
                    function(json) {
                       $('#tariffIndexId').loadSelect(json.tariffTypes);
                       $("#tariffIndexId option:eq(0)").replaceWith("<option value=''><fmt:message key='aimir.contract.tariff.type'/></option>");
                       $("#tariffIndexId").val('');
                       $("#tariffIndexId").selectbox();
                    }
            );
        }

        function getFmtMessage(){
            var fmtMessage = new Array();
            var cnt = 0;

            fmtMessage[cnt++] = '<fmt:message key="aimir.report"/>';                            // 보고서
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.billingData"/>';                // 빌링 데이터
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.dailyTou"/>';                   // 일간 TOU
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.monthlyTou"/>';                 // 월간 TOU
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.currentTou"/>';                 // Current TOU
            fmtMessage[cnt++] = "<fmt:message key='aimir.number'/>";                            // 번호
            fmtMessage[cnt++] = '<fmt:message key="aimir.readingDay"/>';                        // 검침일
            fmtMessage[cnt++] = '<fmt:message key="aimir.customername"/>';                      // 고객명
            fmtMessage[cnt++] = '<fmt:message key="aimir.buildingMgmt.contractNumber"/>';       // 계약번호
            fmtMessage[cnt++] = '<fmt:message key="aimir.meterid"/>';                           // 미터번호
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.totalEnergyUsage"/>';           // 전체 에너지 사용량
            fmtMessage[cnt++] = '<fmt:message key="aimir.contract.demand.amount"/>';            // 계약용량
            fmtMessage[cnt++] = '<fmt:message key="aimir.facilityMgmt.powerConsumption"/>';     // 소비전력
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.totalKvah"/>';                  // Total kVah
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxdmdkvahtime"/>';             // Max kVa Time
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxdmdkvah"/>';                 // Max kVa
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.impphasea"/>';                  // Import PhaseA
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.impphaseb"/>';                  // Import PhaseB
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.impphasec"/>';                  // Import PhaseC 
            fmtMessage[cnt++] = '<fmt:message key="aimir.view.detail"/>';                       // 상세보기
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxdmdkvah.rate1"/>';           // Max Demand kVa Rate1
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxdmdkvah.rate2"/>';           // Max Demand kVa Rate2
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxdmdkvah.rate3"/>';           // Max Demand kVa Rate3
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxdmdkvahtime.rate1"/>';       // Max Demand Time Rate1
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxdmdkvahtime.rate2"/>';       // Max Demand Time Rate2
            fmtMessage[cnt++] = '<fmt:message key="aimir.report.maxdmdkvahtime.rate3"/>';       // Max Demand Time Rate3

            fmtMessage[cnt++] = '<fmt:message key="aimir.buildingMgmt.unUsedEnvironment"/>';    // 사용할수 없는 환경입니다.

            return fmtMessage;
        }

        var reportTitle = '<fmt:message key="aimir.report.dailyTou"/>';
        function setCanvas(type) {
            // 날짜 조건
            if (type == "1" || type == "2" || type == "4") {
                $('#_daily').click();
            } else if (type == "3") {
                $('#_monthly').click();
            }

            // Current TOU 선택 시 비교데이터 체크박스 hide
            if (type == "4") {
                $("#report2").attr("checked", false);
                $("#comparisonCheck").hide();
            } else {
                $("#comparisonCheck").show();
            }

            // title, report type
            if(type == "1") {
                $('#reportTitle').text('<fmt:message key="aimir.report.billingDataRpt"/>');
                $('#reportType').val("daily");
                reportTitle = '<fmt:message key="aimir.report.dailyTou"/>';
            } else if(type == "2") {
                $('#reportTitle').text('<fmt:message key="aimir.report.dailyTouRpt"/>');
                $('#reportType').val("daily");
                reportTitle = '<fmt:message key="aimir.report.dailyTou"/>';
            } else if(type == "3") {
                $('#reportTitle').text('<fmt:message key="aimir.report.monthlyTouRpt"/>');
                $('#reportType').val("monthly");
                reportTitle = '<fmt:message key="aimir.report.monthlyTou"/>';
            } else if(type == "4") {
                $('#reportTitle').text('<fmt:message key="aimir.report.currentTouRpt"/>');
                $('#reportType').val("current");
                reportTitle = '<fmt:message key="aimir.report.currentTou"/>';
            }
        }

        function send() {
            getmvmReportGrid();
        }

        function searchInfo() {
            // commonDateTabButtonType3.jsp 에서 자동 호출되는 function
                null;
        }

        // 조회시 사용될 검색조건
        var getConditionArray = function() {
            var arrayObj = Array();
            var idx = 0;

            arrayObj[idx++] = $('#searchStartDate').val();
            arrayObj[idx++] = $('#searchEndDate').val();
            arrayObj[idx++] = $('#searchDateType').val();
            arrayObj[idx++] = $('#locationId').val();
            arrayObj[idx++] = $('#tariffIndexId').val();
            arrayObj[idx++] = $('#customerName').val();
            arrayObj[idx++] = $('#contractNo').val();
            arrayObj[idx++] = $('#meterId').val();
            arrayObj[idx++] = $('#reportType').val();
            arrayObj[idx++] = supplierId;

            return arrayObj;
        };

        function viewTotalCount(total) {
            $("#totalCount").text(total);
        }

        var winMvmReport;
        function openExcelReport(excelType) {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var condArray = new Array();
            var obj = new Object();
            if(excelType == "details") {
            	condArray[0]		= $("#searchStartDate").val();
            	condArray[1]		= $("#searchEndDate").val();
            	condArray[2]		= $("#searchDateType").val();
            	condArray[3]		= $("#locationId").val();
            	condArray[4]		= $("#tariffIndexId").val();
            	condArray[5]		= $("#reportType").val();
            	condArray[6]		= $("#customerName").val();
            	condArray[7]		= $("#contractNo").val();
            	condArray[8]		= $("#meterId").val();
            	condArray[9]		= $("#report2").is(":checked") ? "Y":"";
            	condArray[10]		= $("#outputRange").val();
            	condArray[11]		= supplierId;
            	
            	obj.condition 		= condArray;
            	obj.excelType		= excelType;
            	
            } else {
            	condArray[0]		= $("#searchStartDate").val();
            	condArray[1]		= $("#searchEndDate").val();
            	condArray[2]		= $("#searchDateType").val();
            	condArray[3]		= $("#locationId").val();
            	condArray[4]		= $("#tariffIndexId").val();
            	condArray[5]		= $("#reportType").val();
            	condArray[6]		= $("#customerName").val();
            	condArray[7]		= $("#contractNo").val();
            	condArray[8]		= $("#meterId").val();
            	condArray[9]		= "0",
            	condArray[10]		= "0",
            	condArray[11]		= supplierId;
            	
            	obj.condition 		= condArray;
            	obj.excelType		= excelType;
            	obj.fmtMessage		= getFmtMessage();
            }
            if(winMvmReport)
                winMvmReport.close();
            winMvmReport = window.open("${ctx}/gadget/mvm/mvmReportDownloadPopup.do", "TouReportExcel", opts);
            winMvmReport.opener.obj = obj;
        }

        // Detail View Window
        var makeDetailWindow = function() {
            var width = $("#detailWindowDiv").width();

            var html = '<div id="detail_window_box" style="padding:10px;">'
                    + '<table class="detail_window">'
                    + '<colgroup>'
                    + '<col width="120px"/>'
                    + '<col width="155px"/>'
                    + '<col width="120px"/>'
                    + '<col width="155px"/>'
                    + '<col width="120px"/>'
                    + '<col width=""/>'
                    + '</colgroup>'
                    + '<tr>'
                    + '<th><fmt:message key="aimir.customername"/></th>'
                    + '<td id="detailCustomerName"></td>'
                    + '<th><fmt:message key="aimir.buildingMgmt.contractNumber"/></th>'
                    + '<td id="detailContractNumber"></td>'
                    + '<th><fmt:message key="aimir.meterid"/></th>'
                    + '<td id="detailMdsId"></td>'
                    + '</tr>'
                    + '<tr>'
                    + '<th><fmt:message key="aimir.contract.wattage"/></th>'
                    + '<td id="detailContractDemand"></td>'
                    + '<th><fmt:message key="aimir.contract.tariff.type"/></th>'
                    + '<td id="detailTariffName"></td>'
                    + '<th><fmt:message key="aimir.location"/></th>'
                    + '<td id="detailLocationName"></td>'
                    + '</tr>'
                    + '<tr><td colspan="6" class="email_dotted"></td></tr>'
                    + '</table>'
                    + '</div>'
                    + '<div id="detailDataDiv" class="margin-t10px" style="overflow:auto;"></div>'
                    + '<div id="detailLastDataDiv" class="margin-t10px" style="overflow:auto; display:none;"></div>';

            var detailWindow = new Ext.Window({
                title: '<fmt:message key="aimir.view.detail"/>',
                id: 'detailWin',
                applyTo:'detailWindowDiv',
                autoScroll: true,
                resizable: true,
                width: width,
                height: 500,
                html: html,
                bodyStyle: {background : "#ffffff"},
                closeAction:'hide'
            });
        };

        // Detail Window 보이기
        function viewDetailWindow(mdevType, mdevId, yyyymmdd, hhmmss, detailContractId, detailMeterId) {

            // Window 사이즈 조정
            $("#detailWindowDiv").width($("#detailWindowDiv").css("width", "100%").width()-10);

            var height;
            if ($("#report2").is(":checked")) {
                height = 810;
            } else {
                height = 470;
            }

            Ext.getCmp('detailWin').setSize($("#detailWindowDiv").width(), height);
            Ext.getCmp('detailWin').setPosition(5, 5);
            $("#mdevType").val(mdevType);
            $("#mdevId").val(mdevId);
            $("#yyyymmdd").val(yyyymmdd);
            $("#hhmmss").val(hhmmss);
            $("#detailContractId").val(detailContractId);
            $("#detailMeterId").val(detailMeterId);
            getBillingDetailData();
            Ext.getCmp('detailWin').show();
        }

        // 상세화면 grid header
        function getHdrMessage(){
            var hdrMessage = new Array();
            var cnt = 0;

            hdrMessage[cnt++] = '<fmt:message key="aimir.report.actImp"/>';                 // Active Import
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.actExp"/>';                 // Active Export
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.reactLagImp"/>';            // Reactive Lag Import
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.reactLeadImp"/>';           // Reactive Lead Import
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.reactLagExp"/>';            // Reactive Lag Export
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.reactLeadExp"/>';           // Reactive Lead Export
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.totEnergy"/>';              // Total Energy
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.energy"/>';                 // Energy
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.totDemandTime"/>';          // Total Demand and Time
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.maxDemandTime"/>';          // Max Demand and Time
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.totCummDemand"/>';          // Total Cummulative Demand
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.cummDemand"/>';             // Cummulative Demand
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.rate1"/>';                  // Rate 1
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.rate2"/>';                  // Rate 2
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.rate3"/>';                  // Rate 3
            hdrMessage[cnt++] = '<fmt:message key="aimir.report.kVAh1"/>';                  // kVAh 1

            return hdrMessage;
        }

        var mvmReportTypeTreeOn = false;
        var mvmReportTypeTree;
        var selectedmvmReportTypeId;
        function getmvmReportTypeTree(){
            var message = getFmtMessage();
            mvmReportTypeTree = new Ext.tree.TreePanel({
                useArrows: true,
                autoScroll: true,
                animate: true,
                enableDD: false,
                containerScroll: true,
                border: true,
                rootVisible: true,
                height: 160,
                width:160,
                loader: new Ext.tree.TreeLoader(),
                root: {
                    nodeType: 'async',
                    text: message[0]+message[1],
                    draggable: false,
                    id: '1',
                    children: [{
                            text: message[2],
                            id:'2',
                            leaf: true
                        }, {
                            text: message[3],
                            id:'3',
                            leaf: true
                        }, {
                            text: message[4],
                            id:'4',
                            leaf: true
                     }]
                },

                listeners: {
                    click: function(node, event) {
                        selectedmvmReportTypeId = node.id;
                        // console.log("selectedmvmReportTypeId",selectedmvmReportTypeId);
                        setCanvas(selectedmvmReportTypeId);
                    }
                }
            });
            mvmReportTypeTreeOn = true;

            // render the tree
            mvmReportTypeTree.render('mvmReportTypeTreeDiv');
            mvmReportTypeTree.getRootNode();
        };

        function getBillingDetailData() {
            emergePre();

            var msg = getHdrMessage();
            var params = {
                    searchStartDate     : $("#yyyymmdd").val(),
                    searchEndDate       : $("#yyyymmdd").val(),
                    searchDateType      : $("#searchDateType").val(),
                    reportType          : $("#reportType").val(),
                    lastData            : $("#report2").is(":checked") ? "Y":"",
                    mdevType            : $("#mdevType").val(),
                    mdevId              : $("#mdevId").val(),
                    detailContractId    : $("#detailContractId").val(),
                    detailMeterId       : $("#detailMeterId").val(),
                    hhmmss              : $("#hhmmss").val(),
                    supplierId          : supplierId
            };

            $.getJSON("${ctx}/gadget/mvm/getMeteringDetailData.do"
                    ,params
                    ,function(json) {
                        var detailData = json.result;

                        if (detailData != null && detailData.length > 0) {
                            //$("#yyyymmddhhmmss").html(detailData[0].yyyymmddhhmmss);
                            $("#detailCustomerName").html(detailData[0].CUSTOMERNAME);
                            $("#detailContractNumber").html(detailData[0].CONTRACTNUMBER);
                            $("#detailMdsId").html(detailData[0].METERID);
                            $("#detailContractDemand").html(detailData[0].CONTRACTDEMAND);
                            $("#detailTariffName").html(detailData[0].TARIFFTYPENAME);
                            $("#detailLocationName").html(detailData[0].LOCATIONNAME);
                        } else {
                            //$("#yyyymmddhhmmss").html("");
                            $("#detailCustomerName").html("");
                            $("#detailContractNumber").html("");
                            $("#detailMdsId").html("");
                            $("#detailContractDemand").html("");
                            $("#detailTariffName").html("");
                            $("#detailLocationName").html("");
                        }

                        $("#detailLastDataDiv").html("");
                        $("#detailLastDataDiv").hide();
                        $("#detailDataDiv").html("");

                        var title = reportTitle + " > " + '<fmt:message key="aimir.report.totalEnergyUsage" /> : ' + detailData[0].detailDate;  

                        //일간 TOU > 전체 에너지 사용량 : 2011.01.01 10:00:00
                        getBillingDetailRender(false, title);

                        if ($("#report2").is(":checked")) {
                            $("#detailLastDataDiv").show();
                            var type;

                            if ($("#searchDateType").val() == DateType.DAILY) {
                                type = '<fmt:message key="aimir.report.comparelastday" />';
                            } else {
                                type = '<fmt:message key="aimir.report.comparelastmonth" />';
                            }

                            var lastTitle = reportTitle + " > " + type + " : " + detailData[0].detailLastDate;  
                            
                            getBillingDetailRender(true, lastTitle);
                        }
                    });
        }

        function getParams() {
            var msg = getHdrMessage();

            var params = {
                    searchStartDate     : $("#yyyymmdd").val(),
                    searchEndDate       : $("#yyyymmdd").val(),
                    searchDateType      : $("#searchDateType").val(),
                    locationId          : $("#locationId").val(),
                    tariffIndexId       : $("#tariffIndexId").val(),
                    reportType          : $("#reportType").val(),
                    customerName        : $("#customerName").val(),
                    contractNo          : $("#contractNo").val(),
                    meterId             : $("#meterId").val(),
                    lastData            : $("#report2").is(":checked") ? "Y":"",
                    mdevType            : $("#mdevType").val(),
                    mdevId              : $("#mdevId").val(),
                    detailContractId    : $("#detailContractId").val(),
                    detailMeterId       : $("#detailMeterId").val(),
                    hhmmss              : $("#hhmmss").val(),
                    msgActImp           : msg[0],
                    msgActExp           : msg[1],
                    msgRactLagImp       : msg[2],
                    msgRactLeadImp      : msg[3],
                    msgRactLagExp       : msg[4],
                    msgRactLeadExp      : msg[5],
                    msgTotEnergy        : msg[6],
                    msgEnergy           : msg[7],
                    msgTotDemandTime    : msg[8],
                    msgMaxDemandTime    : msg[9],
                    msgTotCummDemand    : msg[10],
                    msgCummDemand       : msg[11],
                    msgRate1            : msg[12],
                    msgRate2            : msg[13],
                    msgRate3            : msg[14],
                    msgkVah1            : msg[15],
                    supplierId          : supplierId
            };

            return params;
        }

        var billingDetailGrid;
        var getBillingDetailRender = function(isLast, title) {
            var divId;
            var url;
            //var title;

            if (isLast) {
                divId = "detailLastDataDiv";
                url = "${ctx}/gadget/mvm/getMeteringDetailLastUsageData.do";
                //title = "Last Data";
            } else {
                divId = "detailDataDiv";
                url = "${ctx}/gadget/mvm/getMeteringDetailUsageData.do";
                //title = "Search Data";
            }

            width = $("#" + divId).width();
            if (width < 1500) {
                width = 1500;
            }

            var billingDetailStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: url,
                baseParams: getParams(),
                root:'result',
                fields: ["leftFstTitle", "leftSndTitle", "leftTrdTitle", "topTitle", "value"]
            });

            billingDetailGrid = new Ext.grid.PivotGrid( {
                title : title,
                width : width,
                height : 320,
                renderTo : divId,
                store : billingDetailStore,
                aggregator : 'sum',
                renderer: function(value) {
                    var pattern = /(^0)(.*)/g;

                    if (typeof value != 'string') {
                        value = value + "";
                    }

                    if (pattern.test(value)) {
                        value = value.replace(pattern, '$2');
                    }

                    if (value == "") {
                        value = " ";
                    }

                    return value;
                },
                measure : 'value',

                labelStyle: 'font-weight:bold;',
                viewConfig : {
                    //title : 'Sales Performance',
                    forceFit : false,
                    emptyText : "&nbsp;"
                },

                leftAxis : [ {
                    width : 170,
                    labelStyle: 'font-weight:bold;',
                    dataIndex : 'leftFstTitle'
                }, {
                    width : 60,
                    labelStyle: 'font-weight:bold;',
                    dataIndex : 'leftSndTitle'
                }, {
                    width : 1,
                    labelStyle: 'font-weight:bold;',
                    dataIndex : 'leftTrdTitle'
                } ],

                topAxis : [ {
                    width : 150,
                    dataIndex : 'topTitle'
                } ]
            });
            hide();
        };

        //컬럼 Tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        var mvmReportGridStore;
        var mmvmReportGridColModel;
        var mvmReportGridOn = false;
        var mvmReportGrid;

        //getmvmReportGrid 
        function getmvmReportGrid() {
            var arrayObj = getConditionArray();
            var message  = getFmtMessage();
            var width = $("#mvmReportMaxGridDiv").width(); 

            mvmReportGridStore = new Ext.data.JsonStore({

                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/mvm/getMeteringData.do",
                baseParams:{
                   searchStartDate: arrayObj[0],
                   searchEndDate  : arrayObj[1],
                   searchDateType : arrayObj[2],
                   locationId     : arrayObj[3],
                   tariffIndexId  : arrayObj[4],
                   customerName   : arrayObj[5],
                   contractNo     : arrayObj[6],
                   meterId        : arrayObj[7],
                   reportType     : arrayObj[8],
                   supplierId     : arrayObj[9],
                   pageSize       : 10
                },
                totalProperty: 'total',
                root:'gridDatas',
                fields: [
                { name: 'rownum'        , type: 'Integer'},
                { name: 'dateview'      , type: 'String' },
                { name: 'customerName'  , type: 'String' },
                { name: 'contractNo'    , type: 'String' },
                { name: 'meterId'       , type: 'String' },
                { name: 'energyRateTot' , type: 'String' },
                { name: 'contractDemand', type: 'String' },
                { name: 'demandRateTot' , type: 'String' },
                { name: 'kVah'          , type: 'String' },
                { name: 'maxDmdkVahTime', type: 'String' },
                { name: 'maxDmdkVah'    , type: 'String' },
                { name: 'impkWhPhaseA'  , type: 'String' },
                { name: 'impkWhPhaseB'  , type: 'String' },
                { name: 'impkWhPhaseC'  , type: 'String' },
                { name: 'detailView'    , type: 'String' }
                ],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                             });
                    },
                    load:function(){
                        var count = this.getTotalCount();
                        viewTotalCount(count);
                    }
                }
            });

            var colWidth = (width-50-80)/13-chromeColAdd;
            mvmReportGridColModel = new Ext.grid.ColumnModel({
                columns : [
                    {
                        header : message[5],
                        tooltip : message[5],
                        dataIndex : 'rownum',
                        width : 50-chromeColAdd,
                        align : 'center'
                     }
                     ,{
                        header : message[6],
                        tooltip : message[6],
                        dataIndex : 'dateview',
                        align :'center'
                    }
                    ,{
                        header : message[7],
                        tooltip : message[7],
                        dataIndex : 'customerName',
                        align : 'center'
                    }
                    ,{
                        header : message[8],
                        tooltip : message[8],
                        dataIndex : 'contractNo',
                        align : 'center'
                    }
                    ,{
                        header : message[9],
                        tooltip : message[9],
                        dataIndex : 'meterId',
                        align : 'center'
                    }
                    ,{
                        header : message[10]+"[kWh]",
                        tooltip : message[10]+"[kWh]",
                        dataIndex : 'energyRateTot',
                        align :'right'
                    }
                    ,{
                        header : message[11]+"[kW]",
                        tooltip : message[11]+"[kW]",
                        dataIndex : 'contractDemand',
                        align : 'right'
                    }
                    ,{
                        header : message[12]+"[kW]",
                        tooltip : message[12]+"[kW]",
                        dataIndex : 'demandRateTot',
                        align : 'right'
                    }
                    ,{
                        header : message[13],
                        tooltip : message[13],
                        dataIndex : 'kVah',
                        align : 'right'
                    }
                    ,{
                        header : message[14],
                        tooltip : message[14],
                        dataIndex : 'maxDmdkVahTime',
                        align : 'center'
                    }
                    ,{
                        header : message[15],
                        tooltip : message[15],
                        dataIndex : 'maxDmdkVah',
                        align : 'right'
                    }
                    ,{
                        header : message[16]+"[kWh]",
                        tooltip : message[16]+"[kWh]",
                        dataIndex : 'impkWhPhaseA',
                        align : 'right'
                    }
                    ,{
                        header : message[17]+"[kWh]",
                        tooltip : message[17]+"[kWh]",
                        dataIndex : 'impkWhPhaseB',
                        align : 'right'
                    }
                    ,{
                        header : message[18]+"[kWh]",
                        tooltip : message[18]+"[kWh]",
                        dataIndex : 'impkWhPhaseC',
                        align : 'right'
                    }
                    ,{
                        header : message[19],
                        tooltip : message[19],
                        width : 80 - chromeColAdd - 4,
                        align : 'center',
                        renderer : function(value, metaData, record, index) {
                            var params = record.json
                               var btnHtml = "<a href='#;' onclick='viewDetailWindow(\"{mdevType}\", \"{mdevId}\", \"{yyyymmdd}\",\"{hhmmss}\",\"{detailContractId}\",\"{detailMeterId}\");' class='btn_blue'><span><fmt:message key="aimir.view.detail"/></span></a>";
                                     var tplBtn = new Ext.Template(btnHtml);
                                     return tplBtn.apply({
                                         mdevType: params.mdevType
                                        ,mdevId  : params.mdevId
                                        ,yyyymmdd: params.yyyymmdd
                                        ,hhmmss  : params.hhmmss
                                        ,detailContractId:params.detailContractId
                                        ,detailMeterId : params.detailMeterId});
                       }
                    }
                ],
                defaults : {
                     sortable : true
                    ,menuDisabled : true
                    ,width : colWidth
                    ,renderer : addTooltip
                }
            });

            if (mvmReportGridOn == false) {

                mvmReportGrid = new Ext.grid.GridPanel({
                    id : 'mvmReportMaxMaxGrid',
                    store : mvmReportGridStore,
                    cm : mvmReportGridColModel,
                    autoScroll : false,
                    width : width,
                    height : 368,               
                    stripeRows : true,
                    columnLines : true,
                    loadMask : {
                        msg : 'loading...'
                    },
                    renderTo : 'mvmReportMaxGridDiv',
                    viewConfig : {
                        enableRowBody : true,
                        showPreview : true,
                        emptyText : 'No data to display'
                    } ,
                     bbar : new Ext.PagingToolbar({
                        pageSize : 10,
                        store : mvmReportGridStore,
                        displayInfo : true,
                        displayMsg : ' {0} - {1} / {2}'                
                    })
                });

                mvmReportGridOn  = true;
            } else {
                mvmReportGrid.setWidth(width);
                mvmReportGrid.reconfigure(mvmReportGridStore, mvmReportGridColModel);
                var bottomToolbar = mvmReportGrid.getBottomToolbar();                                                             
                bottomToolbar.bindStore(mvmReportGridStore);
            }
        };
    /*]]>*/
</script>
</head>
<body>
<input type="hidden" id="reportType" name="reportType" value="daily"/>
<input type="hidden" id="mdevType" name="mdevType" />
<input type="hidden" id="mdevId" name="mdevId" />
<input type="hidden" id="detailContractId" name="detailContractId" />
<input type="hidden" id="detailMeterId" name="detailMeterId" />
<input type="hidden" id="yyyymmdd" name="yyyymmdd" />
<input type="hidden" id="hhmmss" name="hhmmss" />
    <div id="wrapper">
        <!-- navigation -->
        <div id="bldgNavi">

            <div class="topAll">
                <div class="search h190">
                    <ul style="overflow: auto;">
                        <li class="Tbu_bold"><fmt:message key="aimir.reporttype" /></li>
                        <li class="margin-t10px">
                            <div id="mvmReportTypeTreeDiv"></div></li>
                    </ul>
                </div>

                <div class="info ml170" id="usageStaticReport">

                    <div>
                        <ul class="billsearch">
                            <li class="Tbu_bold" id="reportTitle"><fmt:message key="aimir.report.billingDataRpt" /></li>
                            <li class="margin-t5px"><%@ include file="/gadget/mvmReportDateTab.jsp"%></li>
                        </ul>
                    </div>
                    <div>
                        <ul id="reportSelect" class="reportSelect">
                            <li class="tit"><fmt:message
                                key="aimir.report.type.select" /></li>
                            <li  class="margin-t10px"><input type="checkbox" class="checkbox" id="report1" name="report1" checked="checked" onclick="return false;"/>
                                <fmt:message key="aimir.report.totalEnergyUsage" /></li>
                            <li id="comparisonCheck"><input type="checkbox" class="checkbox" id="report2" name="report2">
                                <fmt:message key="aimir.report.comparelastday" />(<fmt:message key="aimir.report.comparelastmonth" />)</li>
                        </ul>
                    </div>
                </div>

            </div>

        </div>
        <!--// navigation -->

        <div class="dashedline"></div>
        <div class="searchoption-container">
            <table class="billing">
                <tr>
                    <th><fmt:message key="aimir.location"/></th>
                    <td>
                        <input type="text" id="searchWord" name="searchWord" style="width:140px" value='<fmt:message key="aimir.board.location"/>' />
                        <input type="hidden" id="locationId" value="" />
                    </td>
                    <th><fmt:message key="aimir.contract.tariff.type"/></th>
                    <td><select id="tariffIndexId" style="width:200px;"></select></td>
                    <td></td>
                    <td></td>
                </tr>
                <tr>
                    <th><fmt:message key="aimir.customername"/><!-- 고객명 --></th>
                    <td><input type="text" id="customerName" name="customerName" style="width:140px" /></td>
                    <th><fmt:message key="aimir.buildingMgmt.contractNumber"/><!-- 계약번호 --></th>
                    <td><input type="text" id="contractNo" name="contractNo" style="width:200px" /></td>
                    <th><fmt:message key="aimir.meterid"/><!-- 미터번호 --></th>
                    <td><input type="text" id="meterId" name="meterId" style="width:140px" /></td>
                </tr>

                <tr>
                    <td colspan="2" class="last">
                        <label class="bluebold12pt"><fmt:message key="aimir.result"/> : </label>
                        <label class="bold"><fmt:message key="aimir.mvm.totalCount"/></label>&nbsp;
                        <label class="greenbold" id="totalCount">0</label>
                    </td>

                    <th class="last"><fmt:message key="aimir.report.outputRange"/></th>
                    <td colspan="3" class="last">
                        <span><select id="outputRange" name="outputRange" style="width:68px;"><option value=""></option></select></span>
                        <span class="am_button margin-t2px">
                            <a href="javascript:openExcelReport('excel');" id="btnExcel"><fmt:message key="aimir.button.excel" /></a>
                        </span>
                    </td>
                </tr>
            </table>

        </div>

        <div id="gadget_body">
            <div id="mvmReportMaxGridDiv"></div>
        </div>
        <div id="detailWindowDiv" style="width:100%"></div>
    </div>
    <div id="treeDivAOuter" class="tree-billing auto" style="display:none">
        <div id="treeDivA"></div>
    </div>
</body>
</html>