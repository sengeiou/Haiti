<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="PRAGMA" content="NO-CACHE"/>
    <meta http-equiv="Expires" content="-1"/>
    <title>Report Management Mini Gadget</title>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        TABLE{border-collapse: collapse; width:auto;}
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
        @media screen and (-webkit-min-device-pixel-ratio:0) { 
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        }
        /*.remove {
            background-image:url(../../images/allOff.gif) !important;
        }
        .accept {
            background-image:url(../../images/allOn.png) !important;
        }*/
    </style>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>

    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
        var supplierId = ${supplierId};
        var guageChartDataXml;

        $(document).ready(function(){

        	// Tooltip사용을 위해서 반드시 선언해야 한다.
            Ext.QuickTips.init();
            
            $.getJSON('${ctx}/gadget/system/getCurrentMonthSearchCondition.do'
                    ,{supplierId:supplierId}
                    ,function(json) {
                         var result = json.result;
                         //alert("currentMonth:" + result.currentMonth + ", startDate:" + result.startDate + ", endDate:" + result.endDate);
                         
                         var title = "";
                         title += result.currentMonth + "&nbsp;&nbsp;";
                         //title += "<fmt:message key="aimir.ebs.totalsubstation"/> : " + 61;
                         title += "<fmt:message key="aimir.ebs.totalsubstation"/> : <span id='dtsTotal' style='float:none !important;'></span>";

                         $("#startDate").val(result.startDate);
                         $("#endDate").val(result.endDate);
                         $("#substationTitle").html(title);
                         // 타이틀에 월 넣기
                         // 조회조건 넣기
                         
                         searchData();
                         hide();

                    }
            );

        });

        function searchData() {
            updatePieChart();
            getEbsSuspectedDtsList();
        }

        /* 리스트 START */
        var substationGridOn = false;
        var substationGrid;
        var substationColModel;
        //var checkSelModel;
        var getEbsSuspectedDtsList = function() {
            var width = $("#substationDiv").width();
            var pageSize = 10;

            emergePre();
            var substationStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: pageSize}},
                url: "${ctx}/gadget/system/getEbsSuspectedDtsList.do",
                baseParams:{supplierId:supplierId,
                	        searchStartDate:$("#startDate").val(),
                	        searchEndDate:$("#endDate").val()
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["dtsName", "importEnergyTotal", "consumeEnergyTotal"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });

            var headerSubId = "<fmt:message key="aimir.ebs.substationname"/><br>(<fmt:message key="aimir.loss"/>)";
            var headerImport = "<fmt:message key="aimir.ebs.label.deliveredKwh"/><br>(<fmt:message key="aimir.ebs.lable.toleranceKwh"/>)";
            var headerConsume = "<fmt:message key="aimir.ebs.lable.consumedKwh"/><br>&nbsp;kWh";

            substationColModel = new Ext.grid.ColumnModel({
                columns: [
                    /*{header: "<fmt:message key="aimir.number"/>", width:50,
                     renderer: function(value, metaData, record, index) {
                                   //전체 글수 - (시작글수+글의 줄번호)
                                   return chargeHistoryStore.getTotalCount() - (parseInt(chargeHistoryStore.lastOptions.params.start)+parseInt(index));
                               }
                    },*/
                    //checkSelModel
                    {header: "<span style='text-align:left;font-weight: bold;'><fmt:message key='aimir.number'/></span>", width:30,
                     renderer: function(value, metaData, record, index) {
                                   //전체 글수 - (시작글수+글의 줄번호)
                                  // return suspectedStore.getTotalCount() - (parseInt(suspectedStore.lastOptions.params.start)+parseInt(index));
                                   //return substationStore.getTotalCount() - (parseInt(substationStore.lastOptions.params.start)+parseInt(index));
                                   return (parseInt(substationStore.lastOptions.params.start) + parseInt(index) + 1);                                       
                               }
                    }
                   ,{header: "<span style='text-align:left;font-weight: bold;'>" + headerSubId + "</span>", dataIndex: 'dtsName', width: (width-30)/3, 
                       renderer: addTooltip, tooltip: headerSubId}
                   ,{header: "<span style='text-align:left;font-weight: bold;'>" + headerImport + "</span>", dataIndex: 'importEnergyTotal', width: (width-30)/3+30, align: "right", 
                       renderer: addTooltip, tooltip: headerImport}
                   ,{header: "<span style='text-align:left;font-weight: bold;'>" + headerConsume + "</span>", dataIndex: 'consumeEnergyTotal', width: ((width-30)/3)-30-7, align: "right", 
                       renderer: addTooltip, tooltip: headerConsume}
                ],
                defaults: {
                    sortable: false
                   ,menuDisabled: true
                   ,width: 120
               }
            });

            if(substationGridOn == false) {
                //checkSelModel = new Ext.grid.CheckboxSelectionModel({
                //    checkOnly:true
                //    ,dataIndex: 'contractId'
                //});

                substationGrid = new Ext.grid.GridPanel({
                    //title: '최근 한달 Demand Response History',
                    store: substationStore,
                    colModel : substationColModel,
                    sm: new Ext.grid.RowSelectionModel({ singleSelect:true }),
                    autoScroll:false,
                    width: width,
                    //height: 310,
                    height: 240,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'substationDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: substationStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                substationGridOn = true;
            } else {
                substationGrid.setWidth(width);
                var bottomToolbar = substationGrid.getBottomToolbar();
                substationGrid.reconfigure(substationStore, substationColModel);
                bottomToolbar.bindStore(substationStore);
            }
            hide();
        };

        /* piechart */
        var pieChartDataXml;
        function updatePieChart() {
            emergePre();

            $.getJSON('${ctx}/gadget/system/getEbsDtsStateChartData.do'
                    ,{supplierId:supplierId,
                      searchStartDate:$("#startDate").val(),
                      searchEndDate:$("#endDate").val()}
                    ,function(json) {
                         var list = json.chartDatas;
                         
                         //var total = ((list[0].totalCount != null) ? Number(list[0].totalCount) : 0) + ((list[0].suspectedCount != null) ? Number(list[0].suspectedCount) : 0);
                         var count = list[0].totalCount;
                         $("#dtsTotal").html(count);
                         pieChartDataXml = "<chart "
                             //+ "caption='Emergency' "
                             + "showValues='1' "
                             //+ ((count > 0) ? "showValues='1' " : " ")
                             + "showPercentValues='0' "
                             + "showPercentInToolTip='0' "
                             //+ "pieRadius='90' "
                             + "numberSuffix=' ' "
                             + "pieRadius='70' "
                             + "showZeroPies='1' "
                             + "showLabels='1' "
                             + "showLegend='1' "
                             //+ "startingAngle='90' "
                             //+ "legendPosition='RIGHT' "
                             + "legendPosition='BOTTOM' "
                             + "manageLabelOverflow='1' "
                             + "enableSmartLabels='1' "
                             //+ "isSmartLineSlanted='0' "
                             + "chartLeftMargin = '0' "
                             + "chartRightMargin = '0' "
                             + "chartTopMargin = '0' "
                             + "chartBottomMargin = '0' "
                             + fChartStyle_Common
                             + fChartStyle_Font
                             + fChartStyle_Pie3D_nobg
                             + ">";
                         var labels = "";
                         var svcTypeName = "";

                         if (count > 0) {
                            // labels  += "<set label='<fmt:message key="aimir.ebs.normalsubstation"/>' value='" + list[0].normalCount + "'/>";
                            // labels  += "<set label='<fmt:message key="aimir.ebs.suspectedsubstation"/>' value='" + list[0].suspectedCount + "'/>";
                             labels  += "<set label='<fmt:message key="aimir.normal"/>' value='" + list[0].normalCount + "'/>";
                             labels  += "<set label='<fmt:message key="aimir.ebs.suspicious"/>' value='" + list[0].suspectedCount + "'/>";

                             //labels  += "<set label='<fmt:message key="aimir.ebs.normalsubstation"/>' value='3'/>";
                             //labels  += "<set label='<fmt:message key="aimir.ebs.suspectedsubstation"/>' value='3'/>";
                         } else {
                             labels = "<set label='' value='0' color='E9E9E9' toolText='' />";
                         }

                         pieChartDataXml += labels + "</chart>";
                         pieChartRender();
                         hide();
                    }
            );
        }

        // window resize event
        $(window).resize(function() {
            pieChartRender();
            getEbsSuspectedDtsList();
        });


        function pieChartRender() {
            //pieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "pieChartId", $('#pieChartDiv').width() , "204", "0", "0");
            //pieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "pieChartId", $('#pieChartDiv').width() , "185", "0", "0");
            pieChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "pieChartId", $('#pieChartDiv').width() , "155", "0", "0");
            pieChart.setDataXML(pieChartDataXml);
            pieChart.setTransparent("transparent");
            pieChart.render("pieChartDiv");
        }

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "") {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

    /*]]>*/
    </script>

</head>

<body>
<input type="hidden" id="startDate" style="display:none;"/>
<input type="hidden" id="endDate" style="display:none;"/>
    <div id="wrapper">
        <!-- content -->
        <div class="margin10px">
            <label id="substationTitle" class="check"></label>
            <div id="pieChartDiv" class="divbox"></div>
            <div class="operator_tit_space">
                <label class="check"><fmt:message key="aimir.ebs.susptdsubstationlist"/><!-- Suspected Substation List --></label>
                <!-- <div class="customerlist_title"><span class="icon_title_blue"></span><fmt:message key="aimir.hems.prepayment.eccustomerlist"/>Emergency Credit Mode 고객 리스트 </div>-->
                <!-- <div class="customerlist_btn">
                    <a href="javascript:notifyEmergencyCredit('selected');" class="btn_blue"><span>선택통보</span></a>
                    <a href="javascript:notifyEmergencyCredit('all');" class="btn_blue" ><span>전원통보</span></a>
                </div> -->
            </div>
        </div>
        <!--// content -->
            <div id="substationDiv" class="gadget_body2"></div>
    </div>

</body>

</html>