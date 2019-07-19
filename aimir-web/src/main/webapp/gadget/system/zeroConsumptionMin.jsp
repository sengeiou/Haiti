<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
    <title></title>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <style type="text/css">\
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
        @media screen and (-webkit-min-device-pixel-ratio:0) {

            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }

        html{overflow:auto !important}

        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-panel-bbar table {border-collapse: collapse; width:auto;}
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;            
            font-weight: bold !important;
        }
        .customerlist_title {
            font-weight: bold;
            color: #0f7cd0;
            margin: 0;
            overflow: hidden;
            float: left;
        }
    </style>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <!--<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>-->
    
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/jquery-1.4.2.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/jquery.selectbox.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/autocomplete/jquery.autocomplete.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/commonConstants.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/jquery-ui-1.7.2.min.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/jquery.form.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/FusionCharts.js"></script>
<!--    <script type='text/javascript' charset='utf-8' src="${ctx}/js/common.js"></script>    
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/FusionChartsExportComponent.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/highcharts.js"></script> -->
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/FChartStyle.js"></script> 
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type='text/javascript' charset='utf-8' src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        var guageChartDataXml;
        var supplierId = "${supplierId}";


        var fInterval;
        function delayInit(){
            clearInterval(fInterval);
            Ext.QuickTips.init();
            initZeroConsumptionGrid();
            searchData();
            hide();
        }

        $(document).ready(function(){
            fInterval = setInterval("delayInit()", 300);
        });

        function searchData() {
            updatePieChart();
            zeroConGridLoad();
        }

        /*****************
            검색 옵션용
        ******************/
        // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
        var tabs = {hourly:0,daily:0,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:1,yearly:0};
        
        // 탭명칭 변경시 값입력
        var tabNames = {hourly:'', daily:'', period:'', weekly:'', monthly:'', monthlyPeriod:'', weekDaily:'', seasonal:'', yearly:''};

        var inputDate = new Object();
        inputDate._dateType = DateType.PERIOD;  // 최초 조회탭
        inputDate.dailyStartDate = "${formatDate}";
        inputDate.searchStartDate = "${currentDate}";
        inputDate.searchEndDate   = "${currentDate}";
        tabs.InputDate = inputDate;

        // 공통조회화면 필수 function
        function send() {
            searchData();
        }


        /* piechart */
        var pieChartDataXml;
        function updatePieChart() {
            emergePre();

            $.getJSON('${ctx}/gadget/system/getZeroConsumChartData.do'
                    ,{
                          supplierId : supplierId
                        , searchDateType : $("#searchDateType").val()
                        , searchStartDate : $("#searchStartDate").val()
                        , searchEndDate : $("#searchEndDate").val()
                        , searchWeek : $("#weeklyWeekCombo").val()
                    }
                    ,function(json) {
                        var list = json.chartDatas;
                        pieChartDataXml = "<chart "
                             + "showValues='1' "
                             + "showPercentValues='1' "
                             + "showPercentInToolTip='1' "
                             + "pieRadius='35' "
                             + "showZeroPies='1' "
                             + "showLabels='1' "
                             + "showLegend='1' "
                             + "legendPosition='BOTTOM' "
                             + "manageLabelOverflow='1' "
                             + "enableSmartLabels='1' "
                             + "chartLeftMargin = '0' "
                             + "chartRightMargin = '0' "
                             + "chartTopMargin = '0' "
                             + "chartBottomMargin = '0' "
                             + fChartStyle_Common
                             + fChartStyle_Font
                             + fChartStyle_Pie3D_nobg
                             + ">";
                        var labels = "<set value='"+list.nonezero+"' color='"+fChartColor_Step5[1]+"'/>"
                                   + "<set value='"+list.zero+"' color='"+fChartColor_Step5[4]+"'/>";
                        pieChartDataXml += labels + "</chart>";
                        pieChartRender();
                        hide();

                        $("#today").html(list.today);
                        $("#totalcnt").html(list.totalCountFormat);
                        $("#nonezero").html(list.nonezeroFormat);
                        $("#zero").html(list.zeroFormat);
                    }
            );
        }

        // window resize event
        $(window).resize(function() {
            pieChartRender();
            initZeroConsumptionGrid();            
            searchData();
        });

        function pieChartRender() {
            pieChart = new FusionCharts(
                  "${ctx}/flexapp/swf/fcChart/Pie3D.swf"
                , "pieChartId"
                , $('#fcChartParentDiv').width() - ($('#fcChartLegend').width() + 10)
                , "150"
                , "0"
                , "0"
            );
            pieChart.setDataXML(pieChartDataXml);
            pieChart.setTransparent("transparent");
            pieChart.render("pieChartDiv");
        }

        var zeroConGrid;
        var zeroConGridColModel;
        var zeroConGridStore;
        var zeroConPagingSize = 15;

        // 그리드 로딩
        function zeroConGridLoad(){
            zeroConGridStore.baseParams = {};
            zeroConGridStore.setBaseParam('supplierId', supplierId);
            zeroConGridStore.setBaseParam('searchDateType', $("#searchDateType").val());
            zeroConGridStore.setBaseParam('searchStartDate', $("#searchStartDate").val());
            zeroConGridStore.setBaseParam('searchEndDate', $("#searchEndDate").val());
            zeroConGridStore.setBaseParam('searchWeek', $("#weeklyWeekCombo").val());

            zeroConGridStore.load({params:{start: 0, limit: zeroConPagingSize}});
        }

        // 그리드 스토어 설정
        function initZeroConGridStore(){
            zeroConGridStore = new Ext.data.JsonStore({
                url : "${ctx}/gadget/system/getZeroConsumptionCustomerContracList.do",
                totalProperty : 'totalCount',
                root : 'result',
                fields: [            
                    { name: 'contractNumber', type: 'String' },
                    { name: 'customerName', type: 'String' },   
                    { name: 'mdsId', type: 'String' },               
                    { name: 'serviceTypeCode', type: 'String' },
                    { name: 'serviceTypeName', type: 'String' },
                    { name: 'creditTypeCode', type: 'String' },          
                    { name: 'creditTypeName', type: 'String' },
                    { name: 'tariffTypeName', type: 'String' },
                    { name: 'prepaymentPowerDelay', type: 'String' },
                    { name: 'lastTokenDate', type: 'String' },
                    { name: 'currentCredit', type: 'String' },
                    { name: 'statusName', type: 'String' },
                    { name: 'emergencyCreditStartTime', type: 'String' },
                    { name: 'emergencyCreditMaxDuration', type: 'String' },
                    { name: 'emergencyCreditMaxDate', type: 'String' },
                    { name: 'meterId', type: 'String' },
                    { name: 'mcuId', type: 'String' },
                    { name: 'modelName', type: 'String' }
                ],
                listeners: {
                    beforeload: function(store, options){
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    }
                }        
            }); 
        }

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }        

        // 그리드 컬럼모델 생성
        function initZeroConGridColModel(){
            var mxwidth = 1200;
            var width = $("#zeroConGridDiv").width();

            zeroConGridColModel = new Ext.grid.ColumnModel({
                columns: [
                     {header: "<fmt:message key="aimir.contractNumber"/>", dataIndex: 'contractNumber', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.customername"/>", dataIndex: 'customerName', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                   // ,{header: "<fmt:message key="aimir.hems.prepayment.lastchargedate"/>", dataIndex: 'lastTokenDate', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                   // ,{header: "<fmt:message key="aimir.hems.prepayment.currentbalance"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'currentCredit', width: (width > mxwidth) ? width*(120/mxwidth) : 120, align:'right'}
                    ,{header: "<fmt:message key="aimir.meterid"/>", dataIndex: 'mdsId', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                  //,{header: "<fmt:message key="aimir.address"/>", dataIndex: 'address', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                  //  ,{header: "<fmt:message key="aimir.supply.type"/>", dataIndex: 'serviceTypeName', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                  //  ,{header: "<fmt:message key="aimir.contract.tariff.type"/>", dataIndex: 'tariffTypeName', width: (width > mxwidth) ? width*(120/mxwidth) : 120, renderer: addTooltip}
                  //,{header: "<fmt:message key="aimir.hems.prepayment.limitpower"/>(kWh)", dataIndex: 'prepaymentPowerDelay', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                  //  ,{header: "<fmt:message key="aimir.supplystatus"/>", dataIndex: 'statusName', width: (width > mxwidth) ? width*(120/mxwidth) : 120}
                  //  ,{header: "<fmt:message key="aimir.hems.prepayment.validperiod"/>", dataIndex: 'emergencyCreditMaxDate', width: (width > mxwidth) ? width*(120/mxwidth)-4 : 120-4}
                  //,{header: "<fmt:message key="aimir.hems.prepayment.validperiod"/>", dataIndex: 'emergencyCreditMaxDuration', width: (width > mxwidth) ? width*(120/mxwidth)-4 : 120-4}
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: 120
                    ,align: 'center'
                    ,renderer: addTooltip
               }
            });
        }

        // 그리드 초기화
        function initZeroConsumptionGrid() {
            var zeroConGridWith = $("#contractDiv").width();

            initZeroConGridStore();
            initZeroConGridColModel();
            
            zeroConGrid = new Ext.grid.GridPanel({
                store : zeroConGridStore,
                colModel : zeroConGridColModel,
                sm: new Ext.grid.RowSelectionModel({
                  singleSelect:true
                }),
                autoScroll : false,
                width : zeroConGridWith,
                height : 210,
                stripeRows : true,
                columnLines : true,         
                loadMask : {
                    msg: 'loading...'
                },
                renderTo : 'contractDiv',
                viewConfig : {
                    enableRowBody : true,
                    showPreview : true,
                    emptyText : 'No data to display'
                },
                // paging bar on the bottom
                bbar : new Ext.PagingToolbar({
                    pageSize : zeroConPagingSize,
                    store : zeroConGridStore,
                    displayInfo : true,
                    displayMsg : ' {0} - {1} / {2}',
                    emptyMsg: 'No Data'
                }) 
            });
        };
    /*]]>*/
    </script>
</head>
<body>
    <!-- search-background DIV (S) -->
    <div class="search-bg-withtabs">
        <div class="dayoptions">
            <%@ include file="/gadget/commonDateTab.jsp"%>         
        </div>
    </div>
    <!-- search-background DIV (E) -->    

    <!-- content -->
    <div id="gadget_body">
        <div class="balancebox" style="height: 385px;">
            <div id="fcChartParentDiv">
                <div id="pieChartDiv" style="float:left;"></div>
                <div id="fcChartLegend" class="lgnd_detail_div" style="height:auto; width: 160px;">
                    <div>
                        <ul>
                            <li><label id="lb_today"><font id="today" style="margin-right: 3px;"></font><fmt:message key='aimir.standard2'/></label></li>
                            <li class="bluebold12pt"><label id="lb_total"><fmt:message key='aimir.zeroconsumption.contract.totalcnt'/> : <font id="totalcnt"></font></label></li>
                            <li class="lgnd">
                                <table cellpadding="0" cellspacing="0">
                                    <colgroup>
                                        <col width="20" />
                                        <col width="" />
                                    </colgroup>
                                    <tr>
                                        <td><span class="fChartColor_2"></span></td>
                                        <td><label id="lb_nonzero"><fmt:message key='aimir.zeroconsumption.nonzero.contract'/> : <font id="nonezero"></font></label></td>
                                    </tr>                                    
                                    <tr>
                                        <td><span class="fChartColor_5"></span></td>
                                        <td><label id="lb_zero"><fmt:message key='aimir.zeroconsumption.zero.contract'/> : <font id="zero"></font></label></td>
                                    </tr>
                                </table>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="operator_tit_space" style="clear: both;">
                <div class="customerlist_title">
                    <span class="icon_title_blue"></span>
                    <fmt:message key="aimir.zeroconsumption.contract.list"/>
                </div>
            </div>
            <div id="contractDiv" class="balance_grid"></div>
        </div>
    

    </div>
    <!--// content -->

</body>
</html>