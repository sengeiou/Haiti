<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Metering SLA MaxGadget</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
<%@ include file="/gadget/system/preLoading.jsp"%>
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
<script type="text/javascript" charset="utf-8">

    var supplierId =  ${supplierId};

    var fcSlaChartDataXml;
    var fcSlaChart;
    var fcSLAMissingChartDataXml;
    var fcSLAMissingChart;
    var fcSLAMissingDetailChartDataXml;
    var fcSLAMissingDetailChart;

    var chromeColAdd = 2;
    //탭초기화
    // 값 0 - 숨김처리
    // daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
    var tabs     = {hourly:0,daily:0,period:1,weekly:1,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0,
    		        btn_hourly:0,btn_daily:0,btn_period:1,btn_weekly:1,btn_monthly:1,btn_monthlyPeriod:0,btn_weekDaily:0,btn_seasonal:0,btn_yearly:0,
    		        search_period:1, search_weekly:1,search_monthly:1,
    		        btn_search_period:1, btn_search_weekly:1, btn_search_monthly:1};


    // 탭명칭 변경시 값입력
    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

   $(document).ready(function(){

        $("#meterTimeMax").tabs();
        updateSlaChart();
    });

         //윈도우 리싸이즈시 event
    $(window).resize(function() {
  
        fcChartRender();
        //리싸이즈시 패널 인스턴스 kill & reload
        if($('#_slaTab').is(':visible')){
            meteringSLADetailGrid.destroy();
            meteringSLADetailGridOn = false;
        
             getmeteringSLADetailGrid();

        }else{
           meteringSLAMissingGrid.destroy();
            meteringSLAMissingGridOn = false;
            
            getmeteringSLAMissingGrid();

            meteringMissingDetailGrid.destroy();
            meteringMissingDetailGridOn = false;
            
            getmeteringMissingDetailgGrid(); 
        }
        
        
            
    });   

    $(function() { $('#_slaTab')     .bind('click',function(event) {send();} ); });
    $(function() { $('#_meteringRate')  .bind('click',function(event) {
        send2();
    } ); });
    // SLA  All analysis 탭 ----------------------------------------------- 

    //search 버튼 클릭시 수행되는 func
     function send(){
         emergePre();
		 updateSlaChart();
    }

    function getSLACondition(){
        var condArray = new Array();

        condArray[0] = $('#searchStartDate').val();
        condArray[1] = $('#searchEndDate').val();
        condArray[2] = supplierId;

        return condArray;
    }

    function getMsgSLADetailGrid(){
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.date"/>";//Date
        fmtMessage[1] = "<fmt:message key="aimir.totalInstalledMeters"/>";//TotalInstallMeters
        fmtMessage[2] = "<fmt:message key="aimir.meterstotalaimir"/>";
        fmtMessage[3] = "<fmt:message key="aimir.slameters"/>";//SLA Meters
        fmtMessage[4] = "<fmt:message key="aimir.transutilmeters"/>";//Delivered Meters
        fmtMessage[5] = "SLA %";

        
        return fmtMessage;
    }

    var meteringSLADetailGridStore;
    var meteringSLADetailGridColModel;
    var meteringSLADetailGridOn = false;
    var meteringSLADetailGrid;

    //Sla All anaylsis 그리드
    function getmeteringSLADetailGrid(){

        var arrayObj = getSLACondition();
        var message  = getMsgSLADetailGrid();

        var width = $("#meteringSLADetailGridDiv").width(); 

        meteringSLADetailGridStore = new Ext.data.JsonStore({
             autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/mvm/getMeteringSLAMiniChart.do",
            baseParams:{
                searchStartDate : arrayObj[0],
                searchEndDate   : arrayObj[1],
                supplierId      : arrayObj[2],
            },
            totalProperty: 'total',
            root:'chartData',
             fields: [
            { name: 'xTag', type: 'String' },
            { name: 'commPermittedMeters', type: 'String' },
            { name: 'deliveredMeters', type: 'String' },
            { name: 'permittedMeters', type: 'String' },
            { name: 'slaMeters', type: 'String' },
            { name: 'successRate', type: 'String' } ,
            { name: 'totalGatheredMeters', type: 'String' } ,
            { name: 'totalInstalledMeters', type: 'String' }
            ],
            listeners: {
            beforeload: function(store, options){
            options.params || (options.params = {});
            Ext.apply(options.params, {
                          page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                     });
            }
         }
        });
        meteringSLADetailGridColModel = new Ext.grid.ColumnModel({
           
            columns: [

                {
                    header:message[0],
                    dataIndex:'xTag',
                    width: 20 ,
                    align:'center'
                 }
                 ,{
                    header:message[1],
                    dataIndex:'totalInstalledMeters',
                    width: 20,
                    align:'center'
                    
                }
                ,{
                    header:message[2],
                    dataIndex:'totalGatheredMeters',
                    width: 20 ,
                    align:'center'
                }
                ,{
                    header:message[3],
                    dataIndex:'slaMeters',
                    width: 20,
                    align:'center'
                }
                ,{
                    header:message[4],
                    dataIndex:'deliveredMeters',
                    width: 20,
                    align:'center'
                }
                ,{
                    header:message[5],
                    dataIndex:'successRate',
                    width: 20,
                    align:'center'
                }
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                ,width: ((width-30)/4)-chromeColAdd
            }
        });

        if (meteringSLADetailGridOn == false) {
           
            meteringSLADetailGrid = new Ext.grid.GridPanel({
               
                id: 'meteringSLADetailMaxGrid',
                store: meteringSLADetailGridStore,
                 cm : meteringSLADetailGridColModel,
                autoScroll: false,
                width: width,
                height: 290,
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'meteringSLADetailGridDiv',
                viewConfig: {
                   
                    forceFit:true,
                     scrollOffset: 1,
                     enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: meteringSLADetailGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'                
                })
            });
           
            meteringSLADetailGridOn  = true;

        } else {
            
            meteringSLADetailGrid.setWidth(width);
            meteringSLADetailGrid.reconfigure(meteringSLADetailGridStore, meteringSLADetailGridColModel);
            var bottomToolbar = meteringSLADetailGrid.getBottomToolbar();                                                             
            bottomToolbar.bindStore(meteringSLADetailGridStore);
        }
        
    };
     // SLA  All analysis 탭 End  -------------------------------------


     // Success Rate Anaylsis 탭  ----------------------------------------------- 

    var missedDay    = "";
    var missedReason = "";

    function send2(){
         updateSLAMissingChart();
    
         missedDay    = "";
         missedReason = "";
         meteringMissingDetailGridData=[];
         getmeteringMissingDetailgGrid();

    }

    function getMissingCondition(){
        var condArray = new Array();

        condArray[0] = $('#btn_searchStartDate').val();
        condArray[1] = $('#btn_searchEndDate').val();
        condArray[2] = supplierId;
        condArray[3] = missedDay;
        condArray[4] = missedReason;

        return condArray;
    }

    function getMsgMissingGrid(){
        var fmtMessage = new Array();
        fmtMessage[0] = "<fmt:message key="aimir.commFailedDays"/>";
        fmtMessage[1] = "Count";
        fmtMessage[2] = "%";
        return fmtMessage;
    }

    var meteringSLAMissingGridStore;
    var meteringSLAMissingGridColModel;
    var meteringSLAMissingGridOn = false;
    var meteringSLAMissingGrid;

    //meteringSLADetail 그리드
    function getmeteringSLAMissingGrid(){

        var arrayObj = getMissingCondition();
        var message  = getMsgMissingGrid();

        var width = $("#meteringSLAMissingGridDiv").width(); 

        meteringSLAMissingGridStore = new Ext.data.JsonStore({
            autoLoad: {params:{start: 0, limit: 10}},
            url: "${ctx}/gadget/mvm/getMeteringSLAMissingData.do",
            baseParams:{
                searchStartDate : arrayObj[0],
                searchEndDate   : arrayObj[1],
                supplierId      : arrayObj[2],
            },
            totalProperty: 'totalCnt',
            root:'gridData',
             fields: [
            { name: 'missedDays', type: 'String' },
            { name: 'count', type: 'Integer' },
            { name: 'missedDaysPercnet', type: 'String' }
            ],
            listeners: {
            beforeload: function(store, options){
            options.params || (options.params = {});
            Ext.apply(options.params, {
                          page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                     });
            }
         }
        });

        meteringSLAMissingGridColModel = new Ext.grid.ColumnModel({
           
            columns: [

                {
                    header:message[0],
                    dataIndex:'missedDays',
                    width: 30 ,
                    align:'center'
                 }
                 ,{
                    header:message[1],
                    dataIndex:'count',
                    width: 50,
                    align:'center'
                    
                }
                ,{
                    header:message[2],
                    dataIndex:'missedDaysPercnet',
                    width: 20 ,
                    align:'center'
                }
            ],
            defaults: {
                 sortable: true
                ,menuDisabled: true
                ,width: ((width-30)/4)-chromeColAdd
            }
        });

        if (meteringSLAMissingGridOn == false) {
           
            meteringSLAMissingGrid = new Ext.grid.GridPanel({
               
                id: 'meteringSLAMissingMaxGrid',
                store: meteringSLAMissingGridStore,
                cm : meteringSLAMissingGridColModel,
                selModel    : new Ext.grid.RowSelectionModel({
                    singleSelect : true,
                    listeners    : {
                        rowselect : function(selectionModel, columnIndex, value) {
                            var param = value.data;
                            getMissedDays(param.missedDays);
                        }
                    }
                 }),
                autoScroll: false,
                width: width,
                height: 290,               
                stripeRows : true,
                columnLines: true,
                loadMask: {
                    msg: 'loading...'
                },
                renderTo: 'meteringSLAMissingGridDiv',
                viewConfig: {
                   
                    forceFit:true,
                     scrollOffset: 1,
                     enableRowBody:true,
                    showPreview:true,
                    emptyText: 'No data to display'
                } ,
                 bbar: new Ext.PagingToolbar({
                    pageSize: 10,
                    store: meteringSLAMissingGridStore,
                    displayInfo: true,
                    displayMsg: ' {0} - {1} / {2}'                
                })
            });
           
            meteringSLAMissingGridOn  = true;

        } else {
            
            meteringSLAMissingGrid.setWidth(width);
            meteringSLAMissingGrid.reconfigure(meteringSLAMissingGridStore,
                 meteringSLAMissingGridColModel);
            var bottomToolbar = meteringSLAMissingGrid.getBottomToolbar();                                                             
            bottomToolbar.bindStore(meteringSLAMissingGridStore);
        }
        
    };

    //그리드 row 선택시 event func
    function getMissedDays(MissedDay){
   
        missedDay    = MissedDay;
        missedReason = "";

        if(MissedDay!="Total"){
            updateSLAMissingDetailChart();
            getmeteringMissingDetailData();
        }
    
    }


    function getMissedReason(flexMissedReason){

        missedReason = flexMissedReason;
        getmeteringMissingDetailData();

    }
    // Success Rate Anaylsis 탭 End ----------------------------------------------- 



    // Success Rate Anaylsis Detail -------------------------------------


    function getMsgMissingDetailGrid(){
        var fmtMessage = new Array();
        fmtMessage[0] = "No";
        fmtMessage[1] = "<fmt:message key="aimir.contractNumber"/>";
        fmtMessage[2] = "<fmt:message key="aimir.meterid"/>";
        fmtMessage[3] = "<fmt:message key="aimir.mcuid2"/>";
        fmtMessage[4] = "<fmt:message key="aimir.mcu.lastcomm"/>";
        return fmtMessage;

    }

     function getmeteringMissingDetailData(){

        var arrayObj = getMissingCondition();
        $.getJSON('${ctx}/gadget/mvm/getMeteringSLAMissingDetailGrid.do'
                , {searchStartDate: arrayObj[0],
                   searchEndDate  : arrayObj[1],
                   supplierId     : arrayObj[2],
                   missedDay      : arrayObj[3],
                   missedReason   : arrayObj[4]}
                , function(json) {
                      meteringMissingDetailGridData = json.gridData;
                      getmeteringMissingDetailgGrid();
                  });

     }

        var meteringMissingDetailGridData = [];
        var meteringMissingDetailGridStore;
        var meteringMissingDetailGridColModel;
        var meteringMissingDetailGridOn = false;
        var meteringMissingDetailGrid;

        //meteringSLADetail 그리드
        function getmeteringMissingDetailgGrid(){

            
            var message  = getMsgMissingDetailGrid();

            var width = $("#meteringMissingDetailGridDiv").width(); 

            meteringMissingDetailGridStore = new Ext.data.JsonStore({
                lastOptions:{params:{start: 0, limit: 10}},
                // autoLoad: {params:{start: 0, limit: 10}},
                data: meteringMissingDetailGridData || {},
                // totalProperty: 'totalCnt',
                 root:'',
                 fields: [
                { name: 'no', type: 'Integer' },
                { name: 'contractNumber', type: 'String' },
                { name: 'mdsId', type: 'String' },
                { name: 'deviceSerial', type: 'String' },
                { name: 'lastReadDate', type: 'String' }
                ]/*,
                listeners: {
                beforeload: function(store, options){
                options.params || (options.params = {});
                Ext.apply(options.params, {
                              curPage: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                }
             }*/
            });

            meteringMissingDetailGridColModel = new Ext.grid.ColumnModel({
               
                columns: [

                    {
                        header:message[0],
                        dataIndex:'no',
                        width: 10 ,
                        align:'center'
                     }
                     ,{
                        header:message[1],
                        dataIndex:'contractNumber',
                        width: 20,
                        align:'center'
                        
                    }
                    ,{
                        header:message[2],
                        dataIndex:'mdsId',
                        width: 20 ,
                        align:'center'
                    }
                    ,{
                        header:message[3],
                        dataIndex:'deviceSerial',
                        width: 20 ,
                        align:'center'
                    }
                    ,{
                        header:message[4],
                        dataIndex:'lastReadDate',
                        width: 30 ,
                        align:'center'
                    }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/4)-chromeColAdd
                }
            });

            if (meteringMissingDetailGridOn == false) {
               
                meteringMissingDetailGrid = new Ext.grid.GridPanel({
                   
                    id: 'meteringMissingDetailMaxGrid',
                    store: meteringMissingDetailGridStore,
                    cm : meteringMissingDetailGridColModel,
                    autoScroll: false,
                    width: width,
                    height: 290,               
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'meteringMissingDetailGridDiv',
                    viewConfig: {
                       
                        forceFit:true,
                         scrollOffset: 1,
                         enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 10,
                        store: meteringMissingDetailGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
               
                meteringMissingDetailGridOn  = true;

            } else {
                
                meteringMissingDetailGrid.setWidth(width);
                meteringMissingDetailGrid.reconfigure(meteringMissingDetailGridStore,
                     meteringMissingDetailGridColModel);
                var bottomToolbar = meteringMissingDetailGrid.getBottomToolbar();                                                             
                bottomToolbar.bindStore(meteringMissingDetailGridStore);
            }
            
        };
    // Success Rate Anaylsis Detail -------------------------------------
    function updateSlaChart() {
    	emergePre();
    	
   	    $.getJSON('${ctx}/gadget/mvm/getMeteringSLAMiniChart.do'
   	    	    ,{searchStartDate:$('#searchStartDate').val(), 
   	    	    	searchEndDate:$('#searchEndDate').val(),
   	    	    	supplierId:supplierId}
				,function(json) {
                     var list = json.chartData;
                     fcSlaChartDataXml = "<chart "
	                     + "PYAxisName='<fmt:message key="aimir.count"/>' "
	                     + "SYAxisName='SLA(%)' "
	                     + "SYAxisMaxValue='100' "
	                     + "SYAxisMinValue='0' "
	         			 + "useRoundEdges='0' "
                         + "showValues='0' "
                         + "showLabels='1' "
                         + "showLegend='1' "
                         + "labelDisplay = 'AUTO' "    
             			 + "canvasBorderColor='929292' "
        				 + "canvasBorderThickness='1' "  
        				 + "plotSpacePercent ='60' "  
         				 + "showPlotBorder ='true' "
         				 + "numberSuffix='  ' "        				 
                         + fChartStyle_Common
                         + fChartStyle_Font
                         + fChartStyle_Column2D_nobg
                         + ">";
                	 var categories = "<categories>";

                	 var dataset1 = "<dataset seriesName='SLA <fmt:message key='aimir.meter'/>'>";
                	 var dataset2 = "<dataset seriesName='<fmt:message key='aimir.delivered'/>'>";
                	 var dataset3 = "<dataset seriesName='<fmt:message key='aimir.success.ratio.percent'/>' parentYAxis='S'>";

                     if(list == null || list.length == 0) {
                    	 categories += "<category label=' ' />";
                    	 dataset1 += "<set value='' />";
                    	 dataset2 += "<set value='' />";
                    	 dataset3 += "<set value='' />";
                     } else {
                    	 for( index in list){
                           	 categories += "<category label='"+list[index].xTag+"' />";
                           	 dataset1 += "<set value='"+list[index].slaMeters+"' />";
                           	 dataset2 += "<set value='"+list[index].deliveredMeters+"' />";
                           	 dataset3 += "<set value='"+list[index].successRate+"' />";
                         }
                     }

                     
                     categories += "</categories>";
                     dataset1 += "</dataset>";
                     dataset2 += "</dataset>";
                     dataset3 += "</dataset>";
             
                     fcSlaChartDataXml += categories + dataset1 + dataset2 + dataset3 + "</chart>";

                     fcChartRender();
                     getmeteringSLADetailGrid();
                     hide();
                }
   	    );   		
	}

    function updateSLAMissingChart() {
    	emergePre();
    	
   	    $.getJSON('${ctx}/gadget/mvm/getMeteringSLAMissingData.do'
   	    	    ,{searchStartDate:$('#btn_searchStartDate').val(), 
   	    	    	searchEndDate:$('#btn_searchEndDate').val(),
   	    	    	supplierId:supplierId}
				,function(json) {
                     var list = json.gridData;

                     fcSLAMissingChartDataXml = "<chart "
	                     + "PYAxisName='<fmt:message key="aimir.count"/>' "
	                     + "SYAxisName='(%)' "
	                     + "SYAxisMaxValue='100' "
                         + "showValues='0' "
                         + "showLabels='1' "
                         + "showLegend='1' "
                         + "labelDisplay = 'AUTO' "    
                         + "numberSuffix='  ' "                     
                         + fChartStyle_Common
                         + fChartStyle_Font
                         + fChartStyle_MSColumn3D_nobg
                         + ">";
                	 var categories = "<categories>";                     
                	 var dataset1 = "<dataset seriesName='<fmt:message key='aimir.count'/>'>";
                	 var dataset2 = "<dataset seriesName='<fmt:message key='aimir.percent'/>' parentYAxis='S'>";

                     if(list.length == 1) {
                    	 categories += "<category label=' ' />";
                    	 dataset1 += "<set value='' />";
                    	 dataset2 += "<set value='' />";
                     } else {
                    	 for( var index=0; index<  list.length-1;index++){
                           	 categories += "<category label='"+list[index].missedDays+"' />";
                           	 dataset1 += "<set value='"+list[index].count+"' />";
                           	 dataset2 += "<set value='"+list[index].missedDaysPercnet+"' />";
                         }
                     }
                     
                     categories += "</categories>";
                     dataset1 += "</dataset>";
                     dataset2 += "</dataset>";
                     
                     fcSLAMissingChartDataXml += categories + dataset1 + dataset2 + "</chart>";

                     setDefaultSLAMissingDetailChart();
                     fcChartRender();
                     getmeteringSLAMissingGrid();
                     hide();
                }
   	    );   		
	}

    function updateSLAMissingDetailChart() {
    	emergePre();
    	
   	    $.getJSON('${ctx}/gadget/mvm/getMeteringSLAMissingDetailChart.do'
   	    		,{searchStartDate:$('#btn_searchStartDate').val(), 
   	    	    	searchEndDate:$('#btn_searchEndDate').val(),
   	    	    	supplierId:supplierId,
   	    	    	missedDay:missedDay}
				,function(json) {
                     var list = json.chartData;
                     fcSLAMissingDetailChartDataXml = "<chart "
                    	 + "showValues='1' "
    					 + "showPercentValues='1' "
    					 + "showPercentInToolTip='0' "
    				     + "showZeroPies='0' "
    				     + "pieRadius='90' "
    				     + "showLabels='0' "
    				     + "showLegend='1' "
    				     + "legendPosition='RIGHT' "
    				     + "manageLabelOverflow='1' "
    				     + "numberSuffix='  ' "
    				     + fChartStyle_Common
                    	 + fChartStyle_Font
                         + fChartStyle_Pie3D
                         + ">";
                	 var labels = "";

                	 if(list == null || list.length == 0) {
                		 labels = "<set label='' value='1' color='E9E9E9' toolText='' />"
                	 } else {
	                	 for(index in list) {
	                		 labels += "<set label='"+list[index].label+"' value='"+list[index].data+"' link=\"JavaScript:getMissedReason( '"+list[index].failReason+"' );\" />";
	                	 }
                	 }
                	                     	 	
                	 fcSLAMissingDetailChartDataXml += labels + "</chart>";
                     
                     fcChartRender();

                     hide();
                }
   	    );
    }

    function setDefaultSLAMissingDetailChart() {
    	fcSLAMissingDetailChartDataXml = "<chart "
					       	 + "showValues='1' "
							 + "showPercentValues='1' "
							 + "showPercentInToolTip='0' "
						     + "showZeroPies='0' "
						     + "showLabels='0' "
						     + "showLegend='1' "
						     + "legendPosition='RIGHT' "
						     + "manageLabelOverflow='1' "
						     + "numberSuffix='  ' "
						     + fChartStyle_Common
					    	 + fChartStyle_Font
					         + fChartStyle_Pie3D
					         + ">"
					         + "<set label='' value='1' color='E9E9E9' toolText='' />"
					         + "</chart>";
    }

    function fcChartRender() {
    	if($('#fcSlaChartDiv').is(':visible')) {
	    	fcSlaChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "fcSlaChartId", $('#fcSlaChartDiv').width(), "220", "0", "0");
	        fcSlaChart.setDataXML(fcSlaChartDataXml);
	        fcSlaChart.setTransparent("transparent");
	        fcSlaChart.render("fcSlaChartDiv");
    	}

        if(fcSLAMissingChartDataXml != null) {
        	if($('#fcSLAMissingChartDiv').is(':visible')) {
		        fcSLAMissingChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3DLineDY.swf", "fcSLAMissingChartId", $('#fcSLAMissingChartDiv').width(), "280", "0", "0");
		        fcSLAMissingChart.setDataXML(fcSLAMissingChartDataXml);
		        fcSLAMissingChart.setTransparent("transparent");
		        fcSLAMissingChart.render("fcSLAMissingChartDiv");
        	}
        }

        if(fcSLAMissingDetailChartDataXml != null ) {
        	if($('#fcSLAMissingDetailChartDiv').is(':visible')) {
		        fcSLAMissingDetailChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "fcSLAMissingDetailChartId", $('#fcSLAMissingDetailChartDiv').width() , "280", "0", "0");
		        fcSLAMissingDetailChart.setDataXML(fcSLAMissingDetailChartDataXml);
		        fcSLAMissingDetailChart.setTransparent("transparent");
		        fcSLAMissingDetailChart.render("fcSLAMissingDetailChartDiv");
        	}
        }
    }
    </script>
</head>
<body>

	<!-- 탭 전체 (S) -->
	<div id="meterTimeMax">
		<ul>
			<li><a href="#slaTab"       id="_slaTab">SLA <fmt:message key="aimir.all"/> <fmt:message key="aimir.analysis"/></a></li>
			<li><a href="#meteringRate" id="_meteringRate"><fmt:message key="aimir.meteringrate"/> <fmt:message key="aimir.analysis"/></a></li>
		</ul>


		<!-- 1ST 탭  (S) -->
		<div id="slaTab">

			<div class="search-bg-withouttabs dayoptions-bt">
			   <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
			</div>

			<div class="gadget_body" style="height:210px;">
			<div id="fcSlaChartDiv">
				The chart will appear within this DIV. This text will be replaced by the chart.
			</div>
			
			</div>

			<div class="gadget_body">
                <div id="meteringSLADetailGridDiv"></div>
			</div>

		</div>
		<!-- 1ST 탭  (E) -->



		<!-- 2ND 탭  (S) -->
		<div id="meteringRate">

			<div class="search-bg-withouttabs dayoptions-bt">
			<%@ include file="/gadget/commonDateTabButtonType2.jsp"%>
			</div>


			<div class="gadget_body">
				<div class="headspace_2ndline2"><label class="check"><fmt:message key="aimir.numberofmissing"/></label></div>
				<div class="bodyleft-sla flexlist2 blueline">
					
					<ul><li>
						<div id="fcSLAMissingChartDiv">
							The chart will appear within this DIV. This text will be replaced by the chart.
						</div>
						
					</li></ul>
				</div>

				<div class="bodyright-sla flexlist">
                    <div id="meteringSLAMissingGridDiv" ></div>
				</div>
			</div>
			<div class="clear-width100"></div>

			<div class="gadget_body">
				<div class="headspace_2ndline2"><label class="check"><fmt:message key="aimir.reason"/></label></div>
				<div class="bodyleft-sla flexlist2 blueline">
					<ul><li>
						<div id="fcSLAMissingDetailChartDiv">
							The chart will appear within this DIV. This text will be replaced by the chart.
						</div>
						
					</li></ul>
				</div>


				<div class="bodyright-sla flexlist">
                    <div id="meteringMissingDetailGridDiv"></div>
				</div>
			</div>
	   </div>
		<!-- 2ND 탭  (E) -->
	</div>
	<!-- 탭 전체 (E) -->
</body>
</html>