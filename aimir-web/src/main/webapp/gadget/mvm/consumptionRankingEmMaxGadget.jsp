<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.consumptionRanking"/>(<fmt:message key="aimir.energymeter"/>)</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
        .excel {
            background-image:url(${ctx}/themes/images/customer/icon_excel.png) !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <script type="text/javascript">

        //탭초기화
        var tabs = {hourly:0,daily:0,monthlyPeriod:0,yearly:0};
        var tabNames = {};

        var supplierId = "${supplierId}";
        var serviceType = ServiceType.Electricity;

        // Ext-JS Grid 컬럼사이즈 오류 수정
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

            hide();

            $('#rankingType').val(RankingType.BEST);
            locationTreeGoGo('treeDiv2', 'searchWord', 'locationId');
            getTariffTypes();
            getRankingList();
            
            $("#tariffIndexId").bind("change", function(event) {
                changeUsageRangeCombo();
            });

            getRankingListGrid();
        });

        // 공통날짜조건에서 일자 변경 시 호출하는 함수.
        function _changeStartEndDate() {
            changeUsageRangeCombo();
        }

        // 
        function changeUsageRangeCombo() {
            if ($('#rankingType').val() == RankingType.ZERO) {
                return;
            } else if ($("#tariffIndexId").val() == "") {
                $('#usageRangeCombo').emptySelect();
                $('#rangeCombo').hide();
                $('#rangeInput').show();
                return;
            }

            $.getJSON('${ctx}/gadget/mvm/getTariffSupplySizeComboData.do',
                    {searchEndDate : $("#searchEndDate").val(),
                     supplierId : supplierId,
                     tariffTypeId : $("#tariffIndexId").val()},
                    function(json) {
                         if (json.empty == true) {
                             $('#usageRangeCombo').emptySelect();
                             $('#rangeCombo').hide();
                             $('#rangeInput').show();
                         } else {
                             $('#usageList').val("");
                             $('#rangeCombo').show();
                             $('#rangeInput').hide();
                             $('#usageRangeCombo').loadSelect(json.result);
                             $('#usageRangeCombo').selectbox();
                         }
                    }
            );
        }

        function getTariffTypes() {
            $.getJSON('${ctx}/gadget/mvm/getTariffTypes.do', {serviceType:serviceType, supplierId:supplierId},
                    function(json) {
                        $('#tariffIndexId').loadSelect(json.tariffTypes);
                        $('#tariffIndexId').selectbox();
                    }
            );
        }

        function getRankingList() {
            $.getJSON('${ctx}/gadget/mvm/getRankingList.do',
                    function(json) {
                       $('#rankingList').loadSelect(json.rankingList);
                       $('#rankingList').selectbox();
                    }
            );
        }

        function send() {
            getRankingListGrid();
        };

        // 메세지 처리
        function getFmtMessage() {
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key="aimir.ranking"/>";               // 순위
            fmtMessage[1] = "<fmt:message key="aimir.date"/>";                  // 일자
            fmtMessage[2] = "<fmt:message key="aimir.usage.kwh"/>";             // 사용량[kWh]
            fmtMessage[3] = "<fmt:message key="aimir.contractNumber"/>";        // 계약 번호
            fmtMessage[4] = "<fmt:message key="aimir.customername"/>";          // 고객명
            fmtMessage[5] = "<fmt:message key="aimir.contract.tariff.type"/>";  // 계약 종별
            fmtMessage[6] = "<fmt:message key="aimir.location"/>";              // 지역
            fmtMessage[9] = "<fmt:message key="aimir.alert"/>";
            fmtMessage[10] = "<fmt:message key='aimir.number'/>";               // 번호
            fmtMessage[11] = "EM";                                              // 미터타입
            fmtMessage[12] = "<fmt:message key="aimir.firmware.msg09"/>";       // excel export 조회데이터 없음.
            fmtMessage[13] = "<fmt:message key="aimir.excel.consumRankingEm"/>";
            fmtMessage[14] = "<fmt:message key="aimir.button.excel"/>";
            fmtMessage[15] = "<fmt:message key="aimir.unit.kwh"/>";             // 단위
            fmtMessage[16] = "<fmt:message key="aimir.meterid"/>";              // 미터 아이디

            return fmtMessage;
        }

        // 조회 조건 전달
        function getCondition(){
            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = MeterType.EM;
            condArray[cnt++] = $('#rankingType').val();
            condArray[cnt++] = supplierId;
            condArray[cnt++] = $('#locationId').val();
            condArray[cnt++] = $('#dcuId').val();
            condArray[cnt++] = $('#tariffIndexId').val();
            condArray[cnt++] = $('#contractNo').val();
            condArray[cnt++] = $('#usageList').val();
            if($('#rankingList').val() > 0){
                condArray[cnt++] = $('#rankingList option:selected').text();
            }else{
                condArray[cnt++] = $('#rankingList').val();
            }
            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();
            condArray[cnt++] = $('#usageRangeCombo').val();

            return condArray;
        }

        function changeTab(tabType){
            var itemRanking = document.getElementsByName('rankingTab');

            if(RankingType.BEST == tabType){
                itemRanking[0].id="current";
                itemRanking[1].id="";
                itemRanking[2].id="";
                $('#usageArea').show();
                $('#usageList').show();
            }
            else if(RankingType.WORST == tabType){
                itemRanking[0].id="";
                itemRanking[1].id="current";
                itemRanking[2].id="";
                $('#usageArea').show();
                $('#usageList').show();
            }
            else if(RankingType.ZERO == tabType){
                itemRanking[0].id="";
                itemRanking[1].id="";
                itemRanking[2].id="current";
                $('#usageArea').hide();
                $('#usageList').hide();
            }

            $('#rankingType').val(tabType);
            clearSearchItem();
            send();
        }

        function clearSearchItem(){
            //$('#locationId').option(0);
            $('#searchWord').val("");
            $('#locationId').val("");
            //$('#tariffIndexId').option(0);
            $('#tariffIndexId').val("");
            $('#tariffIndexId').selectbox();
            $('#contractNo').val('');

            $('#usageList').val('');
            $('#usageRangeCombo').emptySelect();
            $('#rangeCombo').hide();
            $('#rangeInput').show();
            
            //$('#rankingList').option(0);
            $('#rankingList').val("");
            $('#rankingList').selectbox();
        }

        //report window(Excel)
        var win;
        function openExcelReport() {
            var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();

            obj.condition = getCondition();
            obj.fmtMessage = getFmtMessage();

            if(win)
                win.close();
            win = window.open("${ctx}/gadget/mvm/consumptionRankingExcelDownloadPopup.do", "ConsumptionRankingEmExcel", opts);
            win.opener.obj = obj;
        }

        /* Ranking 리스트 START */
        var rankingGridOn = false;
        var rankingGrid;
        var rankingStore;
        var rankingColModel;
        function getRankingListGrid() {
            var width = $("#rankingGridDiv").width();
            var pageSize = 15;

            var condArray = getCondition();
            var cnt = 0;
            rankingStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/mvm/getConsumptionRankingList.do",
                baseParams: {
                    meterType    : condArray[cnt++],
                    rankingType  : condArray[cnt++],
                    supplierId   : condArray[cnt++],
                    locationId   : condArray[cnt++],
                    sysId		 : condArray[cnt++],
                    tariffType   : condArray[cnt++],
                    contractNo   : condArray[cnt++],
                    totalUsage   : condArray[cnt++],
                    rankingCount : condArray[cnt++],
                    dateType     : condArray[cnt++],
                    startDate    : condArray[cnt++],
                    endDate      : condArray[cnt++],
                    usageRange   : condArray[cnt++]
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["rankingCnt", "period", "totalUsage", "contractNo", "customerName", "tariffName", "locationName", "mdsId"],
                listeners: {
                    beforeload: function(store, options) {
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    }
                }
            });

            var fmtMessage = getFmtMessage();
            var colWidth = 0;
            var columnArr = [];
            var rankingType = $('#rankingType').val();
            if (RankingType.ZERO == rankingType) {
                columnArr = [
                    {header: fmtMessage[10], dataIndex: 'rankingCnt', width: 70}
                   ,{header: fmtMessage[1], dataIndex: 'period'}
                   ,{header: fmtMessage[3], dataIndex: 'contractNo'}
                   ,{header: fmtMessage[4], dataIndex: 'customerName'}
                   ,{header: fmtMessage[16], dataIndex: 'mdsId'}
                   ,{header: fmtMessage[5], dataIndex: 'tariffName'}
                   ,{header: fmtMessage[6], dataIndex: 'locationName', renderer: addTooltip}
                ];
                colWidth = (width - 70) / 6;
            } else {
                columnArr = [
                    {header: fmtMessage[0], dataIndex: 'rankingCnt', width: 70}
                   ,{header: fmtMessage[1], dataIndex: 'period'}
                   ,{header: fmtMessage[2], dataIndex: 'totalUsage', align: 'right'}
                   ,{header: fmtMessage[3], dataIndex: 'contractNo'}
                   ,{header: fmtMessage[4], dataIndex: 'customerName'}
                   ,{header: fmtMessage[16], dataIndex: 'mdsId'}
                   ,{header: fmtMessage[5], dataIndex: 'tariffName'}
                   ,{header: fmtMessage[6], dataIndex: 'locationName', renderer: addTooltip}
                ];
                colWidth = (width - 70) / 7;
            }

            rankingColModel = new Ext.grid.ColumnModel({
                columns : columnArr,
                defaults : {
                    sortable: true
                   ,menuDisabled: true
                   ,width: colWidth
                   ,align: 'center'
                }
            });

            // header tooltip
            for (var i = 0, colCount = rankingColModel.getColumnCount(); i < colCount; i++) {
                rankingColModel.setColumnTooltip(i, rankingColModel.getColumnHeader(i));
            }

            // ExtJS 그리드 생성
            if (rankingGridOn == false) {
                rankingGrid = new Ext.grid.GridPanel({
                    width: width,
                    height: 435,
                    store: rankingStore,
                    colModel : rankingColModel,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'rankingGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    tbar:['->'
                          ,'-'
                          ,{
                              text: fmtMessage[14],
                              scope: this,
                              iconCls:'excel',
                              handler: function() {
                                  openExcelReport();
                              }
                          }
                    ],
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: rankingStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                rankingGridOn = true;
            } else {
                rankingGrid.setWidth(width);
                var bottomToolbar = rankingGrid.getBottomToolbar();
                rankingGrid.reconfigure(rankingStore, rankingColModel);
                bottomToolbar.bindStore(rankingStore);
            }
        }//Function End

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

        $(window).resize(function() {
            getRankingListGrid();
        });

    </script>
</head>
<body>

    <!--상단탭-->
    <div id="gad_sub_tab">
        <ul>
            <li><a href="javascript:changeTab(RankingType.BEST)" name="rankingTab" id="current"><fmt:message key="aimir.comsumption.best"/></a></li>
            <li><a href="javascript:changeTab(RankingType.WORST)" name="rankingTab" id=""><fmt:message key="aimir.comsumption.worst"/></a></li>
            <li><a href="javascript:changeTab(RankingType.ZERO)" name="rankingTab" id=""><fmt:message key="aimir.comsumption.zero"/></a></li>
        </ul>
    </div>
    <!--상단탭 끝-->


    <!-- search-background DIV (S) -->
    <div class="search-bg-withouttabs with-dayoptions-bt">
        <div class="dayoptions-bt">
        <%@ include file="/gadget/commonDateTabButtonType11.jsp"%>
        </div>
        <div class="dashedline"><ul><li></li></ul></div>


        <div class="searchoption-container">
            <table class="searchoption wfree">
                <tr>
                    <td class="withinput"><fmt:message key="aimir.location.supplier" /></td>
                    <td class="padding-r20px">
                        <input name="searchWord" id='searchWord' style="width:120px" type="text" value='<fmt:message key="aimir.board.location"/>'/>
                        <input type="hidden" id="locationId" value=""></input>
                    </td>
                    <td class="withinput"><fmt:message key="aimir.contract.tariff.type" /></td>
                    <td class="padding-r20px" colspan="2"><select id="tariffIndexId" name="tariffIndexId" style="width:230px"></select></td>
                    <td class="hidden"><input id="rankingType" name="rankingType" type="hidden" value=""></td>
                </tr>
              
                <tr>
                	<td class="withinput"><fmt:message key="aimir.mcuid" /></td>
                	<td><input id="dcuId" name="dcuId" type="text" style="width:100px"></td>
                    <td class="withinput"><fmt:message key="aimir.contract"/> <fmt:message key="aimir.number"/></td>
                    <td><input id="contractNo" name="contractNo" type="text" style="width:100px"></td>
                    <td id="usageArea" class="withinput"><fmt:message key="aimir.usage.section"/></td>
                    <td id="rangeInput" class="padding-r20px">
                        <input id="usageList" name="usageList" type="text" style="width:100px">
                    </td>
                    <td id="rangeCombo" class="padding-r20px" style="display: none;">
                        <select id="usageRangeCombo" name="usageRangeCombo" style="width: 150px;"></select>
                    </td>
                    <td class="withinput"><fmt:message key="aimir.ranking" /> <fmt:message key="aimir.count" /></td>
                    <td><select id="rankingList" name="rankingList" style="width:50px"></select></td>
                    <td>
                        <div id="btn">
                            <ul><li><a href="javascript:;" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></li></ul>
                        </div>
                    </td>
                    
                </tr>
                 
            </table>
            <div id="treeDiv2Outer" class="tree-billing auto"  style="display:none;">
                <div id="treeDiv2"></div>
            </div>
        </div>

    </div>
    <!-- search-background DIV (E) -->


    <div id="gadget_body">
        <div id="rankingGridDiv"></div>
    </div>

</body>
</html>
