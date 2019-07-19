<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

    <title><fmt:message key="aimir.consumptionRanking"/>(<fmt:message key="aimir.watermeter"/>)</title>

    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold !important;
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        //탭초기화
        var tabs = {hourly:0,period:0,monthlyPeriod:0,yearly:0};
        var tabNames = {};

        var supplierId = "${supplierId}";
        var serviceType = ServiceType.Water;

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

            locationTreeGoGo('treeDiv2', 'searchWord', 'locationId');
            getTariffTypeList();

            getRankingListGrid();
        });

        function getTariffTypeList() {
            $.getJSON('${ctx}/gadget/mvm/getTariffTypes.do', {serviceType:serviceType, supplierId:supplierId},
                    function(json) {
                       $('#tariffIndexId').loadSelect(json.tariffTypes);
                       $("#tariffIndexId option:eq(0)").replaceWith("<option value=0><fmt:message key='aimir.contract.tariff.type'/></option>");
                       $("#tariffIndexId").val(0);
                       $("#tariffIndexId").selectbox();
                    }
            );
        }

        function send(){
            getRankingListGrid();
        }

        function getFmtMessage(){
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key='aimir.ranking'/>";         // 순위
            fmtMessage[1] = "<fmt:message key='aimir.customername'/>";    // 고객명
            fmtMessage[2] = "<fmt:message key='aimir.contractNumber'/>"; // 계약 번호
            fmtMessage[3] = "<fmt:message key='aimir.usage.m3'/>";           // 사용량
            fmtMessage[4] = "<fmt:message key='aimir.alert.top10'/>";     // ! Top 10만 조회합니다.
            fmtMessage[9] = "<fmt:message key="aimir.alert"/>";
            fmtMessage[10] = "<fmt:message key="aimir.number"/>";           // 번호
            fmtMessage[11] = "WM";                                          // 미터타입
            return fmtMessage;
        }

        function getCondition(){
            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = MeterType.WM;
            condArray[cnt++] = $('#rankingType').val();
            condArray[cnt++] = supplierId;
            condArray[cnt++] = $('#locationId').val();
            condArray[cnt++] = $('#tariffIndexId').val();
            condArray[cnt++] = $('#usageList').val();
            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();

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
            $('#locationId').option(0);
            $('#tariffIndexId').option(0);
            $('#usageList').val('');
        }

        /* Ranking 리스트 START */
        var rankingGridOn = false;
        var rankingGrid;
        var rankingStore;
        var rankingColModel;
        function getRankingListGrid() {
            var width = $("#rankingGridDiv").width();

            var condArray = getCondition();
            var cnt = 0;
            rankingStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/mvm/getConsumptionRanking.do",
                baseParams: {
                    meterType    : condArray[cnt++],
                    rankingType  : condArray[cnt++],
                    supplierId   : condArray[cnt++],
                    locationId   : condArray[cnt++],
                    tariffType   : condArray[cnt++],
                    totalUsage   : condArray[cnt++],
                    dateType     : condArray[cnt++],
                    startDate    : condArray[cnt++],
                    endDate      : condArray[cnt++]
                },
                root:'result',
                fields: ["rankingCnt", "totalUsage", "contractNo", "customerName"]
            });

            var fmtMessage = getFmtMessage();
            var colWidth = 0;
            var columnArr = [];
            var rankingType = $('#rankingType').val();
            if (RankingType.ZERO == rankingType) {
                columnArr = [
                    {header: fmtMessage[10], dataIndex: 'rankingCnt', width: 70}
                   ,{header: fmtMessage[1], dataIndex: 'customerName', renderer: addTooltip}
                   ,{header: fmtMessage[2], dataIndex: 'contractNo', renderer: addTooltip}
                ];
                colWidth = (width - 70) / 2;
            } else {
                columnArr = [
                    {header: fmtMessage[0], dataIndex: 'rankingCnt', width: 70}
                   ,{header: fmtMessage[1], dataIndex: 'customerName', renderer: addTooltip}
                   ,{header: fmtMessage[2], dataIndex: 'contractNo', renderer: addTooltip}
                   ,{header: fmtMessage[3], dataIndex: 'totalUsage', align: 'right'}
                ];
                colWidth = (width - 70) / 3;
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
                    height: 160,
                    store: rankingStore,
                    colModel : rankingColModel,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'rankingGridDiv',
                    viewConfig: {
                        forceFit : true,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    }
                });
                rankingGridOn = true;
            } else {
                rankingGrid.setWidth(width);
                rankingGrid.reconfigure(rankingStore, rankingColModel);
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

    /*]]>*/
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
        <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
        </div>
        <div class="dashedline"></div>
        <div class="searchoption-container">
            <table class="searchoption wfree">
                <tr>
                    <td class="hidden"><input id="rankingType" type="hidden" value="best"></input></td>
                    <td class="hidden"><input id="fromDate" type="hidden" class="day"></input></td>
                    <td class="hidden"><input id="toDate" type="hidden" class="day"></input></td>
                    <td><input name="searchWord" id='searchWord' style="width:120px" type="text" value='<fmt:message key="aimir.board.location"/>'/>
                        <input type='hidden' id='locationId' value=''></input>
                        </td>
                    <td><select id="tariffIndexId" style="width:230px;"></select></td>
                </tr>
            </table>
            <table class="searchoption wfree">
                <tr>
                    <td id="usageArea" class="withinput"><fmt:message key="aimir.usage.section"/></td>
                    <td><input id="usageList" type="text" style="width:49px"></input></td>
                    <td><em class="am_button"><a href="javascript:;" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></em></td>
                </tr>
            </table>
            <div id="treeDiv2Outer" class="tree-billing auto2" style="display:none;">
                <div id="treeDiv2"></div>
            </div>
        </div>

    </div>
    <!-- search-background DIV (E) -->

    <div class="gadget_body3">
        <div id="rankingGridDiv"></div>
    </div>

</body>
</html>
