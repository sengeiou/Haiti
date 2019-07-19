<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <title>Billing Data Report</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
    @media screen and (-webkit-min-device-pixel-ratio:0) {.tree .ltr ins { margin:0 4px 0 5px !important;}}/*crome,safari hack*/
     /* tree node 의 style 이 어긋나는 부분 수정 */
        .x-tree-node span, .x-tree-node a {
            float:none !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
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

        var supplierId = "";
        var serviceType = ServiceType.Electricity;

        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                    }
                }
        );

        $(document).ready(function(){
            hide();
            getmvmReportTypeTree();
            locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
            makeOutputRangeComboBox();
            getTariffTypeList();

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

            return fmtMessage;
        }

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
            } else if(type == "2") {
                $('#reportTitle').text('<fmt:message key="aimir.report.dailyTouRpt"/>');
                $('#reportType').val("daily");
            } else if(type == "3") {
                $('#reportTitle').text('<fmt:message key="aimir.report.monthlyTouRpt"/>');
                $('#reportType').val("monthly");
            } else if(type == "4") {
                $('#reportTitle').text('<fmt:message key="aimir.report.currentTouRpt"/>');
                $('#reportType').val("current");
            }
        }

        function send() {
            emergePre();

            $.getJSON("${ctx}/gadget/mvm/getMeteringDataCount.do",
                    {searchStartDate:$("#searchStartDate").val(),
                     searchEndDate:$("#searchEndDate").val(),
                     searchDateType:$("#searchDateType").val(),
                     locationId:$("#locationId").val(),
                     tariffIndexId:$("#tariffIndexId").val(),
                     reportType:$("#reportType").val(),
                     supplierId:supplierId},
                    function(json) {
                        $("#totalCount").text(json.result);
                        hide();
                    }
            );
        }

        function searchInfo() {
            // commonDateTabButtonType3.jsp 에서 자동 호출되는 function
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

        var win;
        function openExcelReport(excelType) {
            var opts="width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var condArray = new Array();
            var obj = new Object();
            if(excelType == "details") {
                condArray[0]        = $("#searchStartDate").val();
                condArray[1]        = $("#searchEndDate").val();
                condArray[2]        = $("#searchDateType").val();
                condArray[3]        = $("#locationId").val();
                condArray[4]        = $("#tariffIndexId").val();
                condArray[5]        = $("#reportType").val();
                condArray[6]        = "";
                condArray[7]        = "";
                condArray[8]        = "";
                condArray[9]        = $("#report2").is(":checked") ? "Y":"";
                condArray[10]       = $("#outputRange").val();
                condArray[11]       = supplierId;
                
                obj.condition       = condArray;
                obj.excelType       = excelType;
                
            } else {
                condArray[0]        = $("#searchStartDate").val();
                condArray[1]        = $("#searchEndDate").val();
                condArray[2]        = $("#searchDateType").val();
                condArray[3]        = $("#locationId").val();
                condArray[4]        = $("#tariffIndexId").val();
                condArray[5]        = $("#reportType").val();
                condArray[6]        = "";
                condArray[7]        = "";
                condArray[8]        = "";
                condArray[9]        = 0,
                condArray[10]       = 0,
                condArray[11]       = supplierId;
                
                obj.condition       = condArray;
                obj.excelType       = excelType;
                obj.fmtMessage      = getFmtMessage();
            }
            if(win)
                win.close();
            win = window.open("${ctx}/gadget/mvm/mvmReportDownloadPopup.do", "mvmReportExcel", opts);
            win.opener.obj = obj;
        }

    /*]]>*/
    </script>
</head>
<body>
<input type="hidden" id="reportType" name="reportType" value="daily"/>
    <div id="wrapper">
        <!-- navigation -->
        <div id="bldgNavi">
            <div class="topAll">
                <div>
                    <ul>
                        <li class="Tbu_bold margin-b3px"><fmt:message key="aimir.reporttype" /></li>
                        <li>
                            <div id="mvmReportTypeTreeDiv"></div>
                        </li>
                    </ul>
                </div>
                <div id="usageStaticReport" class="margin-t10px">
                    <div>
                        <ul class="billsearch">
                            <li class="Tbu_bold" id="reportTitle"><fmt:message key="aimir.report.billingDataRpt" /></li>
                            <li><%@ include file="/gadget/mvmReportDateTab.jsp"%></li>
                        </ul>
                    </div>
                    <div>
                        <ul id="reportSelect" class="reportSelect">
                            <li class="tit"><fmt:message
                                key="aimir.report.type.select" /></li>
                            <li><input type="checkbox" class="checkbox" id="report1" name="report1" checked="checked" onclick="return false;"/><fmt:message
                                key="aimir.report.totalEnergyUsage" /></li>
                            <li id="comparisonCheck"><input type="checkbox" class="checkbox" id="report2" name="report2" />
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
                    <td class="mini">
                        <input name="searchWord" id='searchWord' style="width:140px" type="text" value='<fmt:message key="aimir.board.location"/>'/>
                        <input type="hidden" id="locationId" value=""></input>


                    </td>
                    <td class="tou_report_tarifftype"><select id="tariffIndexId" style="width:210px"></select></td>
                </tr>
                <tr>
                    <td colspan="2" class="last">
                        <span class="bluebold12pt"><fmt:message key="aimir.result"/>&nbsp;:&nbsp; </span>
                        <span> <fmt:message key="aimir.mvm.totalCount"/></span>
                        <span class="greenbold">&nbsp;<label id="totalCount">0</label></span>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <span class="padding-t3px"><fmt:message key="aimir.report.outputRange"/>&nbsp;</span>
                        <span><select id="outputRange" name="outputRange" style="width:68px;"><option value=""></option></select></span>
                        <span class="am_button margin-t2px">
                            <a href="javascript:openExcelReport('excel');" id="btnExcel"><fmt:message key="aimir.button.excel" /></a>
                        </span>
                    </td>
                </tr>
            </table>
            <div id="treeDivAOuter" class="tree-billing auto"  style="display:none;">
                <div id="treeDivA"></div>
            </div>
        </div>

    </div>

</body>
</html>