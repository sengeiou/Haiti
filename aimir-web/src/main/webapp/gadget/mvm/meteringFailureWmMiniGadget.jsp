<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
    <title>검침실패(수도)</title>
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
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/


    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:1,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};

    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

    //플렉스객체
    // var flex;

    var fcChartDataXml;
    var fcChart;

    var locationId = "";
    var locationType = "";
    var locationIdforEvent;

    //공급사ID
    var supplierId="${supplierId}";

    $(document).ready(function(){
      Ext.QuickTips.init();
      updateFailureChart();
   
      $('#gridDiv').hide();
      $('#chartDiv').show();
      $('#chartBtn').show();   

      $(function() { $('#gridRadio').bind('click',function(event) { changeDisplay('grid'); } ); });
      $(function() { $('#chartRadio').bind('click',function(event) { changeDisplay('chart'); } ); });

    });

   
      //윈도우 리싸이즈시 event
    $(window).resize(function() {
          
          fcChartRender();
          //리싸이즈시 패널 인스턴스 kill & reload
          meteringFailureMiniGrid.destroy();
          meteringFailureMiniGridOn = false;
        

          meteringFilureTotalGrid.destroy();
          meteringFilureTotalGridInstanceOn = false;
          
          getmeteringfailureRate();
          
    });
    
    function changeDisplay(_type) {

        if(_type == 'grid') {
            $('#chartDiv').hide();
            $('#gridDiv').show();
            $('#chartBtn').hide();
            getmeteringfailureRate();
          
        } else if(_type == 'chart') {
          $('#gridDiv').hide();
          $('#chartDiv').show();
          $('#chartBtn').show();
          fcChartRender();
        }
    }

    /**
     * 공통 send 거래
     * 개별 화면에서 각각 구현해야한다.
     * 조회버튼클릭시 호출하게 된다.
     */
    function send(){
        updateFailureChart();
        getmeteringfailureRate();
       
    }

    //======================================================================================

    function updateFailureChart(locationId, isParent) {
        emergePre();

        if (locationId == null) locationId = "";
        if (isParent == null) isParent = false;

        $.getJSON('${ctx}/gadget/mvm/getMeteringCountListPerLocation.do'
                ,{searchStartDate : $('#searchStartDate').val(),
                  searchEndDate : $('#searchEndDate').val(),
                  meterType : MeterType.WM,
                  locationId : locationId,
                  isParent : isParent,
                  supplierId : supplierId}
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
                                       + "showPercentInToolTip='1' "
                                        + fChartStyle_Common
                                       + fChartStyle_Font
                                       + fChartStyle_Column3D_nobg 
                                       + ">";
                        var categories = "<categories>";
                        var dataset1 = "<dataset seriesName='<fmt:message key="aimir.meteringrate"/>' color='"+fChartColor_Step2[0]+"' >";
                        var dataset2 = "<dataset seriesName='<fmt:message key="aimir.meteringfailurerate"/>' color='"+fChartColor_Step2[1]+"' >";

                        for (var index = 0; index < list.length; index++) {
                            categories += "<category label='" + list[index].locationName + "' />";

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
/*         fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcChartId", $('#chartDiv').width(), "220", "0", "0");
        fcChart.setDataXML(fcChartDataXml);
        fcChart.setTransparent("transparent");
        fcChart.render("chartDiv"); */
      }
    }


    /**
     *  메세지를 조회하기위한 함수
     */
    function getFmtMessage(){
        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.location"/>";    // 지역
        fmtMessage[1] = "<fmt:message key="aimir.meteringfailurerate"/>";   // 검침실패율
        fmtMessage[2] = "<fmt:message key="aimir.total"/>";   // 총계
        fmtMessage[9] = "<fmt:message key="aimir.alert"/>";
        return fmtMessage;
    }

    /**
     조회조건을 조회하기위한 함수
     */
    function getParams(){
        var condArray = new Array();
        condArray[0] = $('#searchStartDate').val();
        condArray[1] = $('#searchEndDate').val();
        condArray[2] = $('#searchDateType').val();
        condArray[3] = MeterType.WM;
        condArray[4] = supplierId;
        return condArray;
    }

    function getmeteringfailureRate(){
      getmeteringFailureMiniGrid();
      getmeteringFailureTotalMiniGrid();
    }
    var meteringFailureMiniGridStore;
    var meteringFailureMiniGridColModel;
    var meteringFailureTreeRootNode;
    var meteringFailureMiniGridOn = false;
    var meteringFailureMiniGrid;
    //meteringFailureMini 그리드
    function getmeteringFailureMiniGrid(){
      
        var arrayObj = getParams();
        var width = $("#resultListGridDiv").width(); 

         meteringFailureMiniGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/mvm/getMeteringFailureRateListWithChildren.do",
            baseParams:{

                searchStartDate  : arrayObj[0],
                searchEndDate    : arrayObj[1],             
                meterType        : arrayObj[3],              
                supplierId       : arrayObj[4]
            },
           // totalProperty: 'total',
            reader: new Ext.data.JsonReader({
               
                root:'result',
                 fields: [
                { name: 'locationName', type: 'String' },
                { name: 'label', type: 'String' }
            ]}),
            root:'result',
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
                    makeMeteringFailureMiniGridTree();
                }
            }
        });
        
    };

      function makeMeteringFailureMiniGridTree(){
        
         var message  = getFmtMessage();
         var width = $("#resultListGridDiv").width(); 
        
         meteringFailureMiniGridColModel = [

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
          
            meteringFailureTreeRootNode = new Ext.tree.AsyncTreeNode({
                text: 'root',
                id: 'result',
                allowChildren: true,
                draggable:true,
                expended:false,
                children: meteringFailureMiniGridStore.reader.jsonData.result
            });
    
            if (!meteringFailureMiniGridOn) {
                meteringFailureMiniGrid = new Ext.ux.tree.TreeGrid({
                width: width,
                height: 180, 
                store:meteringFailureMiniGridStore,
                enableDD: true,
                root: meteringFailureTreeRootNode,               
                columns: meteringFailureMiniGridColModel,
                useArrows: true,  
                renderTo: "resultListGridDiv"
            });
               
                meteringFailureMiniGridOn = true;
            } else {
                meteringFailureMiniGrid.setWidth(width);
                meteringFailureMiniGrid.setRootNode(meteringFailureTreeRootNode);
                var bottomToolbar = meteringFailureMiniGrid.getBottomToolbar();
                bottomToolbar.bindStore(meteringFailureMiniGridStore);
                meteringFailureMiniGrid.render();
            }
    };

    var meteringFilureTotalGridInstanceOn = false;
    var meteringFilureTotalGrid;
    var meteringFilureTotalModel;

    function getmeteringFailureTotalMiniGrid() {

    var arrayObj = getParams();
    var width = $("#totalGridDiv").width();
        var meteringFilureTotalStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/mvm/getMeteringFailureRateListWithChildren.do",
            baseParams:{

                searchStartDate  : arrayObj[0],
                searchEndDate    : arrayObj[1],             
                meterType        : arrayObj[3],              
                supplierId       : arrayObj[4]
            },
            root:'total',
            fields: [
                { name: 'locationName', type: 'String'},
                { name: 'label', type: 'String' }
           ]
        });

         meteringFilureTotalModel = new Ext.grid.ColumnModel({
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

            if (meteringFilureTotalGridInstanceOn == false) {
                meteringFilureTotalGrid = new Ext.grid.GridPanel({
                    id: 'totalGrid',
                    hideHeaders: true,
                    store: meteringFilureTotalStore,
                    colModel : meteringFilureTotalModel,
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
                meteringFilureTotalGridInstanceOn = true;
            } else {
                meteringFilureTotalGrid.setWidth(width);
                meteringFilureTotalGrid.reconfigure(meteringFilureTotalStore, meteringFilureTotalModel);
            }

    }

    function renderGph(value, values){

      var pourcentage = values.failureRate;
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
                var pourcentage = record.json.failureRate;
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
    <%@ include file="../commonDateTab.jsp" %>
  </div>
  <div class="overflow_hidden">
    <div class="floatleft margin-t3px margin-l4">
      <span><input id="gridRadio" type="radio" name="displayType" value="grid" class="radio_space"></span>
      <span class="margin-t3px margin-r10">Grid</span>
      <span><input id="chartRadio" type="radio" name="displayType" value="chart" checked  class="radio_space"></span>
      <span class="margin-t3px">Chart</span>
    </div>
    <div id="chartBtn" class="floatright margin-t5px margin-r10">
       <em class="am_button"><a href="javascript:getParent();" id="btnSearch"><fmt:message key="aimir.parent.location" /></a></em>
    </div>  
  </div>
</div>
<!-- search-background DIV (E) -->


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