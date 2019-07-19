<%@ include file="/taglibs.jsp"%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<%
String mType = request.getAttribute("mvmMiniType").toString();
%>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.meteringdataview"/>(<fmt:message key="aimir.energymeter"/>)</title>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public2.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
        <%-- TreeGrid 관련 js --%>

    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridSorter.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumnResizer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridNodeUI.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridLoader.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGridColumns.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/treegrid/TreeGrid.js"></script>

    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/gridSearch/Ext.ux.grid.Search.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/gridSearch/Ext.ux.grid.RowActions.js"></script>
 
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
        /* ext-js grid header 정렬 */
        .x-grid3-hd-inner{
            text-align: center !important;
            font-weight: bold;
        }
    </style>
    <script type="text/javascript" >/*<![CDATA[*/

        //탭초기화
        var tabs = {hourly:0,monthlyPeriod:0,weekDaily:0};
        var tabNames = {};

        var flex;
        var supplierId = "${supplierId}";
        // user location
        var permitLocationId = "${permitLocationId}";

        var fcChartDataXml;
        var fcChart;

        var locationId = "";
        var locationType = "";
        var locationIdforEvent;


        $(document).ready(function() {
         
            $(function() { $('#gridRadio').bind('click',function(event) { changeDisplay('grid'); } ); });
            $(function() { $('#chartRadio').bind('click',function(event) { changeDisplay('chart'); } ); });

            $('#gridDiv').hide();
            $('#chartDiv').show();
            $('#chartBtn').show();

            updateFailureChart();
        });

          //윈도우 리싸이즈시 event
          $(window).resize(function() {
                
                fcChartRender();
                //리싸이즈시 패널 인스턴스 kill & reload
                if(!(meteringSuccessMiniGrid === undefined)){
                    meteringSuccessMiniGrid.destroy();           	
                }
                meteringSuccessMiniGridOn = false;
              
                if(!(meteringSuccessTotalGrid === undefined)){
                    meteringSuccessTotalGrid.destroy();                    
                }

                meteringSuccessTotalGridInstanceOn = false;
                
                getmeteringsuccessRate();
                
          });

        function getmeteringsuccessRate(){
              getmeteringSuccessMiniGrid();
              getmeteringSuccessTotalMiniGrid();
            }
        function changeDisplay(_type) {
            if(_type == 'grid') {
                $('#chartDiv').hide();
                $('#gridDiv').show();
                $('#chartBtn').hide();
                getmeteringsuccessRate();
            } else if(_type == 'chart') {
                $('#gridDiv').hide();
                $('#chartDiv').show();
                $('#chartBtn').show();
                fcChartRender();
            }
        }

        function send() {
            updateFailureChart();

            getmeteringsuccessRate();
        };

        /**
         * fmt message 전달
         */
        function getFmtMessage() {
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key="aimir.location"/>";     // 지역
            fmtMessage[1] = "<fmt:message key="aimir.meteringrate"/>"; // 검침율
            fmtMessage[2] = "<fmt:message key="aimir.total"/>";        // 총계
            fmtMessage[9] = "<fmt:message key="aimir.alert"/>";

            return fmtMessage;
        }

        /**
         * Condition 전달
         */
        function getParams() {
            var cnt = 0;
            var condArray = new Array();

            if ($("#meterType").val() == "EM") {
                condArray[cnt++] = MeterType.EM;
            } else if ($("#meterType").val() == "GM") {
                condArray[cnt++] = MeterType.GM;
            } else if ($("#meterType").val() == "WM") {
                condArray[cnt++] = MeterType.WM;
            } else if ($("#meterType").val() == "HM") {
                condArray[cnt++] = MeterType.HM;
            } else if ($("#meterType").val() == "VC") {
                condArray[cnt++] = MeterType.VC;
            } else {
                Ext.Msg.alert('<fmt:message key='aimir.message'/>',"<fmt:message key='aimir.invalidArgument'/> "+"<fmt:message key='aimir.metertype'/> [" +$("#meterType").val()+"]");
            }

            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();
            condArray[cnt++] = supplierId;
            if (permitLocationId != "") {
                condArray[cnt++] = permitLocationId;
            }

            return condArray;
        }

        //=============================================================================

        function updateFailureChart(locationId, isParent) {
            emergePre();

            if (locationId == null) locationId = "";
            if (isParent == null) isParent = false;

            var metertype = "EnergyMeter";
            if ($("#meterType").val() == "EM") {
                metertype = MeterType.EM;
            } else if ($("#meterType").val() == "GM") {
                metertype = MeterType.GM;
            } else if ($("#meterType").val() == "WM") {
                metertype = MeterType.WM;
            } else if ($("#meterType").val() == "HM") {
                metertype = MeterType.HM;
            } else if ($("#meterType").val() == "VC") {
                metertype = MeterType.VC;
            }

            $.getJSON('${ctx}/gadget/mvm/getMeteringCountListPerLocation.do'
                    ,{searchStartDate : $('#searchStartDate').val(),
                      searchEndDate : $('#searchEndDate').val(),
                      meterType : metertype,
                      locationId : locationId,
                      isParent : isParent,
                      supplierId : supplierId,
                      permitLocationId : permitLocationId}
                    ,function(json) {
                            var list = json.result;
                            fcChartDataXml = "<chart "
                                           + "chartLeftMargin='0' "
                                           + "chartRightMargin='0' "
                                           + "chartTopMargin='15' "
                                           + "chartBottomMargin='0' "
                                           + "showValues='0' "
                                           + "showZeroPlane='0' "
                                           + "showLegend='1' "
                                           + "stack100percent='1' "
                                           + "showpercentintooltip='1' "
                                           + fChartStyle_Common
                                           + fChartStyle_Font
                                           + fChartStyle_Column3D_nobg 
                                           + ">";
                            var categories = "<categories>";
                            var dataset1 = "<dataset seriesName='<fmt:message key="aimir.meteringrate"/>' color='"+fChartColor_Step2[0]+"' >";
                            var dataset2 = "<dataset seriesName='<fmt:message key="aimir.meteringfailurerate"/>' color='"+fChartColor_Step2[1]+"' >";
                            var len = list.length;
                            for (var index = 0; index < len; index++) {
                                categories += "<category label='"+list[index].locationName+"' />";

                                if (list[index].totalCount > 0) {
                                    if (list[index].children != null && list[index].children.length > 0) {
                                        dataset1 += "<set value='" + list[index].successCount + "' link=\"JavaScript:getChild('" + list[index].locationId + "');\" />";
                                        dataset2 += "<set value='" + list[index].failureCount + "' link=\"JavaScript:getChild('" + list[index].locationId + "');\" />";
                                    } else {
                                        dataset1 += "<set value='" + list[index].successCount + "' />";
                                        dataset2 += "<set value='" + list[index].failureCount + "' />";
                                    }
                                } else {
                                    if (list[index].children != null && list[index].children.length > 0) {
                                        dataset1 += "<set value='0' link=\"JavaScript:getChild('"+list[index].locationId+"');\" />";
                                        dataset2 += "<set value='0' link=\"JavaScript:getChild('"+list[index].locationId+"');\" />";
                                    } else {
                                        dataset1 += "<set value='0' />";
                                        dataset2 += "<set value='0' />";
                                    }
                                }

                                if (index == 0) {
                                    locationIdforEvent = list[index].parent;
                                }
                            }
                            categories += "</categories>";
                            dataset1 += "</dataset>";
                            dataset2 += "</dataset>";

                            fcChartDataXml += categories + dataset1 + dataset2 + "</chart>";

                            fcChartRender();
                            hide();
                    }
            );
        }

        function getParent() {
            updateFailureChart(locationIdforEvent, false);
        }

        function getChild(locationId) {
            updateFailureChart(locationId, true);
        }

        function fcChartRender() {
            if($('#chartDiv').is(':visible')) {
            	fcChart = new FusionCharts({
            		id: 'fcChartId',
        			type: 'StackedColumn3D',
        			renderAt : 'chartDiv',
        			width : $('#chartDiv').width(),
        			height : '220',
        			dataSource : fcChartDataXml
        		}).render();
            	
                /* fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcChartId", $('#chartDiv').width(), "220", "0", "0");
                fcChart.setDataXML(fcChartDataXml);
                fcChart.setTransparent("transparent");
                fcChart.render("chartDiv"); */
            }
        }

        var meteringSuccessMiniGridStore;
        var meteringSuccessMiniGridColModel;
        var meteringSuccessTreeRootNode;
        var meteringSuccessMiniGridOn = false;
        var meteringSuccessMiniGrid;
        //meteringFailureMini 그리드
        function getmeteringSuccessMiniGrid(){
          
            var arrayObj = getParams();
            var width = $("#resultListGridDiv").width(); 

             meteringSuccessMiniGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/mvm/getMeteringSuccessRateListWithChildren.do",
                baseParams:{
            
                    meterType        : arrayObj[0], 
                    searchDateType   : arrayObj[1],
                    searchStartDate  : arrayObj[2],
                    searchEndDate    : arrayObj[3],              
                    supplierId       : arrayObj[4],
                    permitLocationId : arrayObj[5]
                },
               // totalProperty: 'total',
                reader: new Ext.data.JsonReader({
                   
                    root:'',
                     fields: [
                    { name: 'locationName', type: 'String' },
                    { name: 'label', type: 'String' }
                ]}),
                root:'',
                 fields: [
                    { name: 'locationName', type: 'String' },
                    { name: 'label', type: 'String' }
                ],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                    },
                    load: function(store, record, options){
                        makeMeteringSuccessMiniGridTree();
                    }
                }
            });
            
        };

          function makeMeteringSuccessMiniGridTree(){
            
             var message  = getFmtMessage();
             var width = $("#resultListGridDiv").width(); 
            
             meteringSuccessMiniGridColModel = [

                    {
                        header:message[0],                 
                        dataIndex:'locationName',
                        width: width/2-50,
                        align:'center'
                     }
                     ,{
                        header:message[1],
                        dataIndex:'label',
                        width: width/2+30,
                        align:'center',
                        tpl: new Ext.XTemplate('{label:this.gridrenderer}', {
                            gridrenderer: renderGph
                        })
                    }
                ];
              
                meteringSuccessTreeRootNode = new Ext.tree.AsyncTreeNode({
                    text: 'root',
                    id: 'result',
                    allowChildren: true,
                    draggable:true,
                    expended:false,
                    children: meteringSuccessMiniGridStore.reader.jsonData.result
                });
        

                if(meteringSuccessMiniGridOn){
                    meteringSuccessMiniGrid.destroy();
                }

                meteringSuccessMiniGrid = new Ext.ux.tree.TreeGrid({
                    width: width,
                    height: 180, 
                    store:meteringSuccessMiniGridStore,
                    enableDD: true,
                    root: meteringSuccessTreeRootNode,               
                    columns: meteringSuccessMiniGridColModel,
                    useArrows: true,  
                    renderTo: "resultListGridDiv"
                });
           
                meteringSuccessMiniGridOn = true;                
        };

        var meteringSuccessTotalGridInstanceOn = false;
        var meteringSuccessTotalGrid;
        var meteringSuccessTotalModel;

        function getmeteringSuccessTotalMiniGrid() {

        var arrayObj = getParams();
        var width = $("#totalGridDiv").width();
            var meteringSuccessTotalStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/mvm/getMeteringSuccessRateListWithChildren.do",
                baseParams:{

                    meterType        : arrayObj[0], 
                    searchDateType   : arrayObj[1],
                    searchStartDate  : arrayObj[2],
                    searchEndDate    : arrayObj[3],              
                    supplierId       : arrayObj[4],
                    permitLocationId : arrayObj[5]
                },
                root:'total',
                fields: [
                    { name: 'locationName', type: 'String'},
                    { name: 'label', type: 'String' }
               ]
            });

             meteringSuccessTotalModel = new Ext.grid.ColumnModel({
                    columns: [
                       {
                                   
                        dataIndex:'locationName',
                        width: width/2-60,
                        align:'center'
                     }
                     ,{
                      
                        dataIndex:'label',
                        width: width/2+30,
                        align:'center',
                        renderer: rendertotalGph
                    }
                      
                    ],
                    defaults: {
                         sortable: true
                        ,menuDisabled: true
                    }
                });

                if (meteringSuccessTotalGridInstanceOn == false) {
                    meteringSuccessTotalGrid = new Ext.grid.GridPanel({
                        id: 'totalGrid',
                        hideHeaders: true,
                        store: meteringSuccessTotalStore,
                        colModel : meteringSuccessTotalModel,
                        autoScroll: false,
                        width: width,
                        height: 34,
                        stripeRows : true,
                        columnLines: true,
                        loadMask: {
                            msg: 'loading...'
                        },
                        renderTo: 'totalGridDiv',
                        viewConfig: {
                            forceFit:true,
                            scrollOffset: 1,
                            enableRowBody:true,
                            showPreview:true,
                            emptyText: 'No data to display'
                        }
                    });
                    meteringSuccessTotalGridInstanceOn = true;
                } else {
                    meteringSuccessTotalGrid.setWidth(width);
                    meteringSuccessTotalGrid.reconfigure(meteringSuccessTotalStore, meteringSuccessTotalModel);
                }

        }

            function renderGph(value, values){

              var pourcentage = values.successRate;
              var w = Math.floor(pourcentage);

             var html = '<div class="x-progress-wrap"  style="margin: 5px;">'+
                  '<div class="x-progress-inner">'+
                      '<div class="x-progress-bar" style="width:'+w+'%">'+
                      '</div>'+
                      '<div class="x-progress-text x-progress-text-back">'+
                          '<div>' + values.label+'</div>'+
                      '</div>'+
                  '</div>'+
              '</div>';

             return html;
            };

            function rendertotalGph(value, metadata, record, rowIndex, colIndex, store){
          
                var items = store.data.items[rowIndex].data;
                console.log("record",record);
                var pourcentage = record.json.successRate;
                var w = Math.floor(pourcentage);

                var html = '<div class="x-progress-wrap" style="width: 90% !important" >'+
                    '<div class="x-progress-inner">'+
                        '<div class="x-progress-bar" style="width:'+w+'%">'+
                        '</div>'+
                        '<div class="x-progress-text x-progress-text-back">'+
                            '<div>' +items.label+'</div>'+
                        '</div>'+
                    '</div>'+
                '</div>';

               return html;
            };
    /*]]>*/
    </script>
</head>
<body>

<!-- search-background DIV (S) -->
<div class="overflow_hidden">
    <div class="dayoptions border-bottom padding-b3px">
        <%@ include file="/gadget/commonDateTab.jsp"%>
    </div>

    <div class="overflow_hidden">
        <div class="floatleft margin-t3px margin-l10">
            <span><input id="gridRadio" type="radio" name="displayType" value="grid" class="radio_space"></span>
            <span class="margin-t3px margin-r10">Grid</span>
            <span><input id="chartRadio" type="radio" name="displayType" value="chart" checked  class="radio_space"></span>
            <span class="margin-t3px">Chart</span>
        </div>
        <div id="chartBtn" class="floatright margin-t5px margin-r10">
           <em class="am_button"><a href="javascript:getParent();" id="btnSearch" class="on"><fmt:message key="aimir.parent.location" /></a></em>
        </div>
    </div>

</div>
<!-- search-background DIV (E) -->


<input type="hidden" id="meterType" value=<%=mType%> >


<div class="gadget_body3">
    <div id="chartDiv">
        The chart will appear within this DIV. This text will be replaced by the chart.
    </div>
    <div id="gridDiv">
    <div id="resultListGridDiv"> </div>
    <div id="totalGridDiv"> </div>
    </div>
</div>

</body>
</html>