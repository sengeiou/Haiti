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
    <link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <style type="text/css">
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
        @media screen and (-webkit-min-device-pixel-ratio:0) {
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

        var guageChartDataXml;
        var supplierId = "${supplierId}";

        $(document).ready(function(){
            Ext.QuickTips.init();
            hide();
            searchData();
        });

        function searchData() {
            updatePieChart();
            getEmergencyCreditContractData();
        }

        /* 리스트 START */
        var contractGridOn = false;
        var contractGrid;
        var contractColModel;
        var checkSelModel;
        var getEmergencyCreditContractData = function() {
            var width = $("#contractDiv").width();

            emergePre();
            var contractStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/prepaymentMgmt/getEmergencyCreditContractList.do",
                baseParams: {
                    supplierId : supplierId,
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["contractId", "contractNumber", "customerName", "mdsId", "lastTokenDate", "limitDate", "limitDuration"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            if(contractGridOn == false) {
                checkSelModel = new Ext.grid.CheckboxSelectionModel({
                    checkOnly:true
                    ,dataIndex: 'contractId'
                });

                contractColModel = new Ext.grid.ColumnModel({
                    columns: [
                        /*{header: "<fmt:message key="aimir.number"/>", width:50,
                         renderer: function(value, metaData, record, index) {
                                       //전체 글수 - (시작글수+글의 줄번호)
                                       return chargeHistoryStore.getTotalCount() - (parseInt(chargeHistoryStore.lastOptions.params.start)+parseInt(index));
                                   }
                        },*/
                        checkSelModel
                       ,{header: "<fmt:message key="aimir.contractNumber"/>", dataIndex: 'contractNumber', width: (width-20)/5, renderer: addTooltip}
                       ,{header: "<fmt:message key="aimir.name"/>", dataIndex: 'customerName', width: (width-20)/5, renderer: addTooltip}
                       //,{header: "<fmt:message key="aimir.address"/>", dataIndex: 'address', width: (width-20)/5, renderer: addTooltip}
                       ,{header: "<fmt:message key="aimir.meterid"/>", dataIndex: 'mdsId', width: (width-20)/5, renderer: addTooltip}
                       ,{header: "<fmt:message key="aimir.hems.prepayment.lastchargedate"/>", dataIndex: 'lastTokenDate', width: (width-20)/5, renderer: addTooltip}
                       ,{header: "<fmt:message key="aimir.hems.prepayment.validperiod"/>", dataIndex: 'limitDate', width: (width-20)/5, renderer: addTooltip}
                    ],
                    defaults: {
                        sortable: false
                       ,menuDisabled: true
                       ,width: 120
                   }
                });

                contractGrid = new Ext.grid.GridPanel({
                    //title: '최근 한달 Demand Response History',
                    store: contractStore,
                    colModel : contractColModel,
                    sm: checkSelModel,
                    autoScroll:false,
                    width: width,
                    height: 310,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'contractDiv',
                    viewConfig: {
                    	forceFit:true,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: 10,
                        store: contractStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                contractGridOn = true;
            } else {
                contractGrid.setWidth(width);
                var bottomToolbar = contractGrid.getBottomToolbar();
                contractGrid.reconfigure(contractStore, contractColModel);
                bottomToolbar.bindStore(contractStore);
            }
            hide();
        };

        /* piechart */
        var pieChartDataXml;
        function updatePieChart() {
            emergePre();

            $.getJSON('${ctx}/gadget/prepaymentMgmt/getPrepaymentContractStatusChartData.do'
                    ,{supplierId : supplierId}
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
                        var labels = "";

                        labels = "<set value='"+list.normal+"' color='"+fChartColor_Step5[0]+"'/>"
                               + "<set value='"+list.suspended+"' color='"+fChartColor_Step5[5]+"'/>"
                               + "<set value='"+list.stop+"' color='"+fChartColor_Step5[1]+"'/>"
                               + "<set value='"+list.cancel+"' color='"+fChartColor_Step5[2]+"'/>"
                               + "<set value='"+list.pause+"' color='"+fChartColor_Step5[3]+"'/>"
                               + "<set value='"+list.unknown+"' color='"+fChartColor_Step5[4]+"'/>";
                               

                        if (list.normal == 0 && list.pause == 0 && list.stop == 0 && list.cancel == 0 && list.suspended == 0) {
                            labels = "<set value='1' color='E9E9E9' toolText='' />";
                        }

                        pieChartDataXml += labels + "</chart>";
                        pieChartRender();
                        hide();

                        $("#today").html(list.today);
                        $("#totalcnt").html(list.totalCountFormat);
                        $("#normal").html(list.normalFormat);
                        $("#suspended").html(list.suspendedFormat);
                        $("#stop").html(list.stopFormat);
                        $("#cancel").html(list.cancelFormat);
                        $("#pause").html(list.pauseFormat);
                        $("#unknown").html(list.unknownFormat);
                    }
            );
        }

        // window resize event
        $(window).resize(function() {
            pieChartRender();
            getEmergencyCreditContractData();
        });

        function pieChartRender() {
            //pieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "pieChartId", $('#pieChartDiv').width() , "204", "0", "0");
            pieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "pieChartId",
                    $('#fcChartParentDiv').width() - ($('#fcChartLegend').width() + 10), "185", "0", "0");
            pieChart.setDataXML(pieChartDataXml);
            pieChart.setTransparent("transparent");
            pieChart.render("pieChartDiv");
        }

        function notifyEmergencyCredit(mode) {
            if (mode == 'all') {
                checkSelModel.selectAll();
            }

            var checkedArr = checkSelModel.getSelections();

            if (checkedArr.length > 0) {
                var paramArr = new Array();
                for (var i = 0 ; i < checkedArr.length ; i++) {
                    paramArr.push(checkedArr[i].get("contractId"));
                }

                var params = {
                        "checkedData" : paramArr.join(",")
                };

                $.post("${ctx}/gadget/prepaymentMgmt/notifyEmergencyCredit.do",
                       params,
                       function(json) {
                           if (json.result == "success") {
                               Ext.MessageBox.alert('Notification', "<fmt:message key='aimir.hems.info.drMgmt'/>", function() {checkSelModel.clearSelections(); });
                           }else {
                               Ext.MessageBox.alert('Notification', "<fmt:message key='aimir.hems.error.drMgmt'/>", function() { });
                           }

                           return;
                       }
                );
            } else {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.select.row.no"/>", function() {});
            }
        }

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
    /*]]>*/
    </script>
</head>
<body>
<div id="wrapper">
    <!-- content -->
    <div class="balancebox">
        <div id="fcChartParentDiv" class="lgnd_detail_parent">
            <div id="pieChartDiv" class="float_left"></div>
            <div id="fcChartLegend" class="lgnd_detail_div" style="height:auto; width:160px;">
                <div>
                    <ul>
                        <li><label id="lb_today"><font id="today" style="margin-right: 3px;"></font><fmt:message key='aimir.standard2'/></label></li>
                        <li class="bluebold12pt"><label id="lb_total"><fmt:message key='aimir.prepayment.contract.totalcnt'/> : <font id="totalcnt"></font></label></li>
                        <li class="lgnd">
                            <table cellpadding="0" cellspacing="0">
                                <colgroup>
                                    <col width="20" />
                                    <col width="" />
                                </colgroup>
                                <tr>
                                    <td><span class="fChartColor_1">&nbsp;</span></td>
                                    <td><label id="lb_normal"><fmt:message key='aimir.normal'/> : <font id="normal"></font></label></td>
                                </tr>
                                <tr>
                                    <td><span class="fChartColor_6"></span></td>
                                    <td><label id="lb_suspended"><fmt:message key='aimir.suspended'/> : <font id="suspended"></font></label></td>
                                </tr>
                                <tr>
                                    <td><span class="fChartColor_2"></span></td>
                                    <td><label id="lb_stop"><fmt:message key='aimir.pause'/> : <font id="stop"></font></label></td>
                                </tr>
                                <tr>
                                    <td><span class="fChartColor_3"></span></td>
                                    <td><label id="lb_cancel"><fmt:message key='aimir.cancel2'/> : <font id="cancel"></font></label></td>
                                </tr>
                                <tr>
                                    <td><span class="fChartColor_4"></span></td>
                                    <td><label id="lb_pause"><fmt:message key='aimir.temporaryPause'/> : <font id="pause"></font></label></td>
                                </tr>
                                <tr>
                                    <td><span class="fChartColor_5"></span></td>
                                    <td><label id="lb_unknown"><fmt:message key='aimir.unknown'/> : <font id="unknown"></font></label></td>
                                </tr>
                            </table>
                        </li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="operator_tit_space" style="clear: both;">
            <div class="customerlist_title"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.eccustomerlist"/><!-- Emergency Credit Mode 고객 리스트 --></div>

         </div>
        <div id="contractDiv" class="balance_grid"></div>
    </div>
    <!--// content -->
</div>
</body>
</html>