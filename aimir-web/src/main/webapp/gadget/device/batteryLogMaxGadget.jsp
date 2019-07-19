<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
    contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="aimir.batteryvoltage"/></title>
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css"/>
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
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
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8"  src="${ctx}/js/extjs/ext-all.js"></script>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <script type="text/javascript" >/*<![CDATA[*/

	    //탭초기화
	    var tabs = {daily:0,hourly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
	    var tabNames = {};

        var supplierId =  ${supplierId};
        var modemId;
        var batteryLogModemType;

        var searchModemType = "";
        var leftChartDataXml;
    	var leftChart;
    	var rightChartDataXml;
    	var rightChart;
    	var logChartDataXml;
    	var logChart;
        var chromeColAdd = 2;

        function initVoltage() {

            getBatteryVoltage();
        }

        $(document).ready(function() {

                    
            $("#batteryLogTab").subtabs();

            $(function() { $('#periodSearch') .bind('click',function(event) { send2(DateType.PERIOD ); } ); });
            $(function() { $('#weeklySearch') .bind('click',function(event) { send2(DateType.WEEKLY ); } ); });
            $(function() { $('#monthlySearch').bind('click',function(event) { send2(DateType.MONTHLY); } ); });

            $('#periodSearch').parent().css('display','inline');
            $('#weeklySearch').parent().css('display','inline');
            $('#monthlySearch').parent().css('display','inline');

            $('#batteryStatus').append("<option value=1><fmt:message key='aimir.normal'/></option>");
            $('#batteryStatus').append("<option value=2><fmt:message key='aimir.abnormal'/></option>");
            $('#batteryStatus').append("<option value=3><fmt:message key='aimir.replacement'/></option>");
            $('#batteryStatus').append("<option value=4><fmt:message key='aimir.unknown'/></option>");

            $('#batteryVoltSign').append("<option value='"+signType.bigger+"'>"+signType.bigger+"</option>");
            $('#batteryVoltSign').append("<option value='"+signType.equal+"'>"+signType.equal+"</option>");
            $('#batteryVoltSign').append("<option value='"+signType.smaller+"'>"+signType.smaller+"</option>");

            $('#operatingDaySign').append("<option value='"+signType.bigger+"'>"+signType.bigger+"</option>");
            $('#operatingDaySign').append("<option value='"+signType.equal+"'>"+signType.equal+"</option>");
            $('#operatingDaySign').append("<option value='"+signType.smaller+"'>"+signType.smaller+"</option>");

            $("#modemType").selectbox();
            $("#powerType").selectbox();
            $("#batteryStatus").selectbox();
            $("#batteryVoltSign").selectbox();
            $("#operatingDaySign").selectbox();

            locationTreeGoGo('treeDivA', 'searchWord', 'meterLocation');
            send();
             $('#log').hide();
        });        

        //윈도우 리싸이즈시 event
        $(window).resize(function() {
            if(!(batteryVoltageGrid === undefined)){
                batteryVoltageGrid.destroy();
            }
            batteryVoltageGridOn = false;
            getBatteryVoltage();

            if(!(batterylogDetailGrid === undefined)){
                batterylogDetailGrid.destroy();                
            }
            batterylogDetailGridOn = false;
            getBatteryLogDetail();

            fcChartRender();
        });  
    
        function send(){
        	updateBatteryLogPieChart();
            getBatteryVoltage();
        };

        var fmtMessage = new Array();

        fmtMessage[0] = "<fmt:message key="aimir.normal"/>";
        fmtMessage[1] = "<fmt:message key="aimir.abnormal"/>";
        fmtMessage[2] = "<fmt:message key="aimir.replacement"/>";
        fmtMessage[3] = "<fmt:message key="aimir.unknown"/>";
        fmtMessage[4] = "<fmt:message key="aimir.date"/>";
        fmtMessage[5] = "<fmt:message key="aimir.volt.current"/>";
        fmtMessage[6] = "<fmt:message key="aimir.volt.offset"/>";
        fmtMessage[9] = "<fmt:message key="aimir.alert"/>";

        fmtMessage[10] = "<fmt:message key="aimir.number"/>";
        fmtMessage[11] = "<fmt:message key="aimir.batterystatus"/>";
        fmtMessage[12] = "<fmt:message key="aimir.modemid"/>";
        fmtMessage[13] = "<fmt:message key="aimir.modem.type"/>";
        fmtMessage[14] = "<fmt:message key="aimir.power.type"/>";
        fmtMessage[15] = "<fmt:message key="aimir.meterlocation"/>";
        fmtMessage[16] = "<fmt:message key="aimir.checktime"/>";
        fmtMessage[17] = "<fmt:message key="aimir.batteryvoltage"/>";
        fmtMessage[18] = "<fmt:message key="aimir.operating.day"/>";
        fmtMessage[19] = "<fmt:message key="aimir.activetime.minute"/>";
        fmtMessage[20] = "<fmt:message key="aimir.reset.count"/>";
  
        /**
         * Condition 전달
         */
        function getCondition(){
            var cnt = 0;
            var condArray = new Array();
            condArray[cnt++] = supplierId;
            condArray[cnt++] = $('#modemId').val();
            condArray[cnt++] = $('#modemType').val();
            condArray[cnt++] = $('#modemType option:selected').text();
            condArray[cnt++] = $('#powerType').val() != "" ? $('#powerType option:selected').text() : "";
            condArray[cnt++] = $('#meterLocation').val() != 0 ? $('#meterLocation').val() : "";
            condArray[cnt++] = $('#batteryStatus').val();
            condArray[cnt++] = $('#batteryVoltSign').val();
            condArray[cnt++] = $('#batteryVolt').val();
            condArray[cnt++] = $('#operatingDaySign').val();
            condArray[cnt++] = $('#operatingDay').val();

            return condArray;
        }

        /**
         * fmt message 전달
         */
        function getFmtMessage2(){
            var fmtMessage = new Array();

            fmtMessage[0] = "<fmt:message key="aimir.date"/>";
            fmtMessage[1] = "<fmt:message key="aimir.batteryvoltage"/>";
            fmtMessage[2] = "<fmt:message key="aimir.volt.current"/>";
            fmtMessage[3] = "<fmt:message key="aimir.volt.offset"/>";
            fmtMessage[9] = "<fmt:message key="aimir.alert"/>";

            return fmtMessage;
        }

        /**
         * Condition 전달
         */
        function getCondition2(){
            var cnt = 0;
            var condArray = new Array();
            condArray[cnt++] = supplierId;
            condArray[cnt++] = batteryLogModemType;
            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();

            return condArray;
        }

        function updateBatteryLogPieChart() {
        	var _powerType = $('#powerType').val() != "" ? $('#powerType option:selected').text() : "";
        	var _meterLocation = $('#meterLocation').val() != 0 ? $('#meterLocation').val() : "";

        	//var _modemTypeName = "";
        	if($('#modemType option:selected').text() == '' || $('#modemType option:selected').text() == '<fmt:message key="aimir.all"/>'){
        		_modemTypeName = "ZEU-PLS";
        		searchModemType = "All";
            }else{
            	_modemTypeName = $('#modemType option:selected').text();
            	searchModemType = "Detail";
            }

        	batteryLogModemType = $('#modemType').val();
            
     		emergePre();
     			$.getJSON('${ctx}/gadget/device/getBatteryLogList.do'
     		    	    ,{supplierId:supplierId
     		    	    	, modemId:$('#modemId').val()
     		    	    	, modemType:$('#modemType').val()
     		    	    	, modemTypeName:$('#modemType option:selected').text()
     		    	    	, powerType:_powerType
     		    	    	, meterLocation:_meterLocation
     		    	    	, batteryStatus:$('#batteryStatus').val()
     		    	    	, batteryVoltSign:$('#batteryVoltSign').val()
     		    	    	, batteryVolt:$('#batteryVolt').val()
     		    	    	, operatingDaySign:$('#operatingDaySign').val()
     		    	    	, operatingDay:$('#operatingDay').val()
         		    	    }
     				,function(json) {
     	                var chart1Data = json.result.chart1;
         				var chart2Data = json.result.chart2; 
         				leftChartDataXml = "<chart "
             				+ "caption='"+_modemTypeName+"' "
    						+ "chartLeftMargin='5' "
    						+ "chartRightMargin='5' "
    						+ "chartTopMargin='5' "
    						+ "chartBottomMargin='10' "
    						+ "pieRadius='120' "
    						+ "showZeroPies='1' "
    						+ "showLabels='0' "
    						+ "showValues='0' "
    						+ "showLegend='1' "
                            + "legendNumColumns='4' "
    						+ "showPercentInToolTip='0' "
    						 + "manageLabelOverflow='1' "
    						 + "numberSuffix='  ' "
    						//+ "legendPosition='RIGHT' "
    						+ fChartStyle_Common
    						+ fChartStyle_Font
    						+ fChartStyle_Pie3D
    						+ ">";
         				 var leftLabels = "";

	                   	 if(chart1Data.total > 0) {
	                   		leftLabels += "<set label='<fmt:message key='aimir.normal'/>' value='"+chart1Data.normal+"' />"
	   	                  	 	+ "<set label='<fmt:message key='aimir.abnormal'/>' value='"+chart1Data.abnormal+"' />"
	   	                  	 	+ "<set label='<fmt:message key='aimir.replacement'/>' value='"+chart1Data.replacement+"' />"
	   	                  	 	+ "<set label='<fmt:message key='aimir.unknown'/>' value='"+chart1Data.unknown+"' />";
	                   	 } else {
	                   		leftLabels = "<set label='' value='1' color='E9E9E9' toolText='' />";
                     	 }
	                       	 	
	                   	 leftChartDataXml += leftLabels + "</chart>";

	                   	if($('#modemType option:selected').text() == '' || $('#modemType option:selected').text() == '<fmt:message key="aimir.all"/>'){		                   	
		                   	rightChartDataXml = "<chart "
		                   		+ "caption='Repeater' "
	    						+ "chartLeftMargin='5' "
	    						+ "chartRightMargin='5' "
	    						+ "chartTopMargin='5' "
	    						+ "chartBottomMargin='10' "
	    						+ "pieRadius='120' "
	    						+ "showZeroPies='1' "
	    						+ "showLabels='0' "
	    						+ "showValues='0' "
	    						+ "showLegend='1' "
                                + "legendNumColumns='4' "
	    						+ "showPercentInToolTip='0' "
	    						 + "manageLabelOverflow='1' "
	    						 + "numberSuffix='  ' "
	    						//+ "legendPosition='RIGHT' "
	    						+ fChartStyle_Common
	    						+ fChartStyle_Font
	    						+ fChartStyle_Pie3D
	    						+ ">";
	         				 var rightLabels = "";
	
		                   	 if(chart2Data.total > 0) {
		                   		rightLabels += "<set label='<fmt:message key='aimir.normal'/>' value='"+chart2Data.normal+"' />"
		   	                  	 	+ "<set label='<fmt:message key='aimir.abnormal'/>' value='"+chart2Data.abnormal+"' />"
		   	                  	 	+ "<set label='<fmt:message key='aimir.replacement'/>' value='"+chart2Data.replacement+"' />"
		   	                  	 	+ "<set label='<fmt:message key='aimir.unknown'/>' value='"+chart2Data.unknown+"' />";
		                   	 } else {
		                   		rightLabels = "<set label='' value='1' color='E9E9E9' toolText='' />";
	                     	 }
		                       	 	
		                   	rightChartDataXml += rightLabels + "</chart>";
	                   	} else {
	                   		rightChartDataXml = "<chart "
	                        	+ "chartLeftMargin='0' "
	    						+ "chartRightMargin='0' "
	    						+ "chartTopMargin='5' "
	    						+ "chartBottomMargin='0' "
	      	                    + "showValues='0' "
	     	                    + "showLabels='1' "
	     	                    + "showLegend='1' "
                                + "legendNumColumns='4' "
	     	                    + "yaxismaxvalue='5'"
	     	                    + "labelDisplay = 'WRAP' "
	     	                    + "yAxisMaxValue='100' "
	     	                   	+ "numberSuffix='  ' "
	     	                    + fChartStyle_legendScroll
	     	                    + fChartStyle_Common
	     	                    + fChartStyle_Font
	     	                    + fChartStyle_MSColumn3D_nobg
	     	                    + ">";
	     	                 var categories = "<categories>";
	         				 var normalDS = "<dataset seriesName='<fmt:message key='aimir.normal'/>'>";
	         				 var abnormalDS = "<dataset seriesName='<fmt:message key='aimir.abnormal'/>'>";
	         				 var replacementDS = "<dataset seriesName='<fmt:message key='aimir.replacement'/>'>";
	         				 var unknownDS = "<dataset seriesName='<fmt:message key='aimir.unknown'/>'>";

	         				 for(index in chart2Data) {
	         					categories += "<category label='"+chart2Data[index].location+"' />";
	         					var total = chart2Data[index].normal
	         								+ chart2Data[index].abnormal
	         								+ chart2Data[index].replacement
	         								+ chart2Data[index].unknown;
	         					normalDS += "<set value='"+chart2Data[index].normal / total * 100+"' />";
	         					abnormalDS += "<set value='"+chart2Data[index].abnormal / total * 100+"' />";
	         					replacementDS += "<set value='"+chart2Data[index].replacement / total * 100+"' />";
	         					unknownDS += "<set value='"+chart2Data[index].unknown / total * 100+"' />";
	         				 }

	         				categories += "</categories>";
	         				normalDS += "</dataset>";
	         				abnormalDS += "</dataset>";
	         				replacementDS += "</dataset>";
	         				unknownDS += "</dataset>";

	         				rightChartDataXml += categories + normalDS + abnormalDS + replacementDS + unknownDS + "</chart>";
	                   	}

	                   	fcChartRender();

                     	hide();
     	            }
     		    );
     	}

        function updateLogChart() {
        	emergePre();
 			$.getJSON('${ctx}/gadget/device/getBatteryLogDetailList.do'
 		    	    ,{supplierId:supplierId 		    	    	
 		    	    	, modemType:batteryLogModemType
 		    	    	, modemId:modemId
 		    	    	, dateType:$('#searchDateType').val()
 		    	    	, fromDate:$('#searchStartDate').val()
 		    	    	, toDate:$('#searchEndDate').val()
     		    	    }
 				,function(json) {
 	                var chartData = json.result.grid; 
 	               logChartDataXml = "<chart "
	                    + "showValues='0' "
	                    + "showLabels='1' "
	                    + "showLegend='1' "
	                    + "numberSuffix='  ' "
	                    + "labelStep='"+(chartData.length / 4)+"' "
	                    + "labelDisplay='STAGGER' "
	                    + fChartStyle_Common
	                    + fChartStyle_Font
	                    + fChartStyle_MSCol3DLine_nobg
	                    + ">";
                    var categories = "<categories>";
   		            var dataset1 = "<dataset seriesName='<fmt:message key='aimir.batteryvoltage'/>'>";
   		            var dataset2 = "<dataset seriesName='<fmt:message key='aimir.volt.current'/>'>";
   		            var dataset3 = "<dataset seriesName='<fmt:message key='aimir.volt.offset'/>'>";

   		            if(chartData.length > 0) {
	   		         	for( index in chartData){
	 	                  	categories += "<category label='"+chartData[index].date+"' />";
	 	                   	dataset1 += "<set value='"+chartData[index].batteryVolt+"' />";
	 	                   	dataset2 += "<set value='"+chartData[index].voltCurrent+"' />";
	 	                   	dataset3 += "<set value='"+chartData[index].voltOffset+"' />";
		                }
   		            } else {
   		            	categories += "<category label=' ' />";
 	                   	dataset1 += "<set value='0' />";
 	                   	dataset2 += "<set value='0' />";
 	                   	dataset3 += "<set value='0' />";
   		            }
	                categories += "</categories>";
	                dataset1 += "</dataset>";
	                dataset2 += "</dataset>";
	                dataset3 += "</dataset>";
                       	 	
                   	logChartDataXml += categories + dataset1 + dataset2 + dataset3 + "</chart>";

                   	fcChartRender();

                 	hide();
 	            }
 		    );
        }

        function fcChartRender() {
        	if($('#fcPieChartDiv').is(':visible')) {
        		leftChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "myLeftChartId", $('#fcLeftChart').width() , "240", "0", "0");
        		leftChart.setDataXML(leftChartDataXml);
        		leftChart.setTransparent("transparent");
        		leftChart.render("fcLeftChart");

        		if(searchModemType == "All") {
	        		rightChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Pie3D.swf", "myRightChartId", $('#fcRightChart').width() , "240", "0", "0");
	        		rightChart.setDataXML(rightChartDataXml);
	        		rightChart.setTransparent("transparent");
	        		rightChart.render("fcRightChart");
        		} else {
        			rightChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "myRightChartId", $('#fcRightChart').width() , "240", "0", "0");
	        		rightChart.setDataXML(rightChartDataXml);
	        		rightChart.setTransparent("transparent");
	        		rightChart.render("fcRightChart");
        		}
        	}
        	
        	if($('#fcLogChart').is(':visible')) {
        		logChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSLine.swf", "myLogChartId", $('#fcLogChart').width(), "240", "0", "0");
        		logChart.setDataXML(logChartDataXml);
        		logChart.setTransparent("transparent");
        		logChart.render("fcLogChart");
        	}
        }

         var batteryVoltageGridStore;
         var batteryVoltageGridColModel;
         var batteryVoltageGridOn = false;
         var batteryVoltageGrid;

        function getBatteryVoltage(){
            var arrayObj = getCondition();

            var width = $("#batteryVoltageDiv").width(); 

             batteryVoltageGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/device/getBatteryVoltageGridData.do",
                baseParams:{
                    supplierId      : arrayObj[0],
                    modemId         : arrayObj[1],
                    modemType       : arrayObj[2],
                    modemTypeName   : arrayObj[3],
                    powerType       : arrayObj[4],
                    meterLocation   : arrayObj[5],
                    batteryStatus   : arrayObj[6],
                    batteryVoltSign : arrayObj[7],
                    batteryVolt     : arrayObj[8],
                    operatingDaySign: arrayObj[9],
                    operatingDay    : arrayObj[10],
                    pageSize        : 10
                },
                totalProperty: 'total',
                root:'griddata',
                 fields: [
                { name: 'no', type: 'String' },
                { name: 'batteryStatus', type: 'String' },
                { name: 'modemId', type: 'String' },
                { name: 'displayData', type: 'String' },
                { name: 'powerType', type: 'String' },
                { name: 'meterLocation', type: 'String' } ,
                { name: 'checkTime', type: 'String' } ,
                { name: 'batteryVolt', type: 'String' } ,
                { name: 'operatingDay', type: 'String' },
                { name: 'activeTime', type: 'String' },
                { name: 'resetCount', type: 'String' }
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
            
            batteryVoltageGridColModel = new Ext.grid.ColumnModel({
                columns: [
                    
                    {header: fmtMessage[10], 
                       dataIndex: 'no', 
                       align:'center', 
                       width: 60 
                     }
                     ,{header: fmtMessage[11], 
                       dataIndex: 'batteryStatus', 
                       align:'center', 
                       width: 120 
                     }
                    ,{header: fmtMessage[12],
                        width: 185, 
                        dataIndex: 'modemId', 
                        align: 'center'
                     }
                     ,{header: fmtMessage[13],
                        width: 185, 
                        dataIndex: 'displayData', 
                        align: 'center'
                     }
                     ,{header: fmtMessage[14],
                        width: 185, 
                        dataIndex: 'powerType', 
                        align: 'center'
                     }
                     ,{header: fmtMessage[15],
                        width: 185, 
                        dataIndex: 'meterLocation', 
                        align: 'center'
                     }
                     ,{header: fmtMessage[16],
                        width: 185, 
                        dataIndex: 'checkTime', 
                        align: 'center'
                     }
                     ,{header: fmtMessage[17],
                        width: 185, 
                        dataIndex: 'batteryVolt', 
                        align: 'center'
                     }
                     ,{header: fmtMessage[18],
                        width: 185, 
                        dataIndex: 'operatingDay', 
                        align: 'center'
                     }
                    ,{header: fmtMessage[19],
                        width: 185, 
                        dataIndex: 'activeTime', 
                        align: 'center'
                     }
                     ,{header: fmtMessage[20],
                        width: 185, 
                        dataIndex: 'resetCount', 
                        align: 'right'
                     }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/4)-chromeColAdd
   
                },

            });
            if (batteryVoltageGridOn == false) {
                batteryVoltageGrid = new Ext.grid.GridPanel({
                    id: 'batteryVoltageMaxGrid',
                    store: batteryVoltageGridStore,
                    colModel : batteryVoltageGridColModel,
                    selModel    : new Ext.grid.RowSelectionModel({
                        singleSelect : true,
                        listeners    : {
                            rowselect : function(selectionModel, columnIndex, value) {
                                var param = value.data;
                                gridItemClick(param.modemId,param.displayData);
                            }
                        }
                     }),
                    autoScroll: false,
                    width: width,
                    height: 295,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'batteryVoltageDiv',
                    viewConfig: {
                        forceFit:true,
                        scrollOffset: 1,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 10,
                        store: batteryVoltageGridStore,
                        displayInfo: true,

                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
                batteryVoltageGridOn  = true;
            } else {
                
                batteryVoltageGrid.setWidth(width);
                batteryVoltageGrid.reconfigure(batteryVoltageGridStore, batteryVoltageGridColModel);
                var bottomToolbar = batteryVoltageGrid.getBottomToolbar();                                                                 
                bottomToolbar.bindStore(batteryVoltageGridStore);
            }
        }

         function gridItemClick(val, val2) {
            modemId = val;
            batteryLogModemType = val2;            
            $('#log').show();
            updateLogChart();
            getBatteryLogDetail();
        }

         var batterylogDetailGridStore;
         var batterylogDetailGridColModel;
         var batterylogDetailGridOn = false;
         var batterylogDetailGrid;

        function getBatteryLogDetail(){
            var arrayObj = getCondition2();

            var width = $("#batteryLogDetailDiv").width(); 

             batterylogDetailGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/device/getBatteryLogDetailDataList.do",
                baseParams:{
                    supplierId      : arrayObj[0],
                    modemType       : arrayObj[1],
                    modemId         : modemId,
                    dateType        : arrayObj[2],
                    fromDate        : arrayObj[3],
                    toDate          : arrayObj[4],
                    pageSize        : 8
                },
                totalProperty: 'total',
                root:'griddata',
                 fields: [
                { name: 'date', type: 'String' },
                { name: 'decimalBatteryVolt', type: 'String' },
                { name: 'decimalVoltCurrent', type: 'String' },
                { name: 'decimalVoltOffset', type: 'String' }
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
            
            batterylogDetailGridColModel = new Ext.grid.ColumnModel({
                columns: [
                    
                    {header: fmtMessage[4], 
                       dataIndex: 'date', 
                       align:'center', 
                       width: 60 
                     }
                     ,{header: fmtMessage[17], 
                       dataIndex: 'decimalBatteryVolt', 
                       align:'center', 
                       width: 110 
                     }
                    ,{header: fmtMessage[5],
                        width: 85, 
                        dataIndex: 'decimalVoltCurrent', 
                        align: 'center'
                     }
                     ,{header: fmtMessage[6],
                        width: 85, 
                        dataIndex: 'decimalVoltOffset', 
                        align: 'center'
                     }
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-20)/4)-chromeColAdd
   
                },

            });
            if (batterylogDetailGridOn == false) {
                batterylogDetailGrid = new Ext.grid.GridPanel({
                    id: 'batteryLogDetailMaxGrid',
                    store: batterylogDetailGridStore,
                    colModel : batterylogDetailGridColModel,
                    autoScroll: false,
                    width: width,
                    height: 260,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'batteryLogDetailDiv',
                    viewConfig: {
                        forceFit:true,
                        scrollOffset: 1,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    } ,
                     bbar: new Ext.PagingToolbar({
                        pageSize: 8,
                        store: batterylogDetailGridStore,
                        displayInfo: true,

                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
                batterylogDetailGridOn  = true;
            } else {
                
                batterylogDetailGrid.setWidth(width);
                batterylogDetailGrid.reconfigure(batterylogDetailGridStore, batterylogDetailGridColModel);
                var bottomToolbar = batterylogDetailGrid.getBottomToolbar();                                bottomToolbar.bindStore(batterylogDetailGridStore);
            }
        }

            function send2(_dateType){
            // 조회조건 검증
            if(!validateSearchCondition(_dateType))return false;
            updateLogChart();
            getBatteryLogDetail();
        }

    /*]]>*/
    </script>
</head>

<body>

<!--상단검색-->
<div class="search-bg-withouttabs">

	<div class="searchoption-container">
		<table class="searchoption wfree">
			<tr>
				<td class="withinput"><fmt:message key="aimir.modemid"/></td>
				<td id="search-date" class="padding-r20px"><input id="modemId" type="text" style="width:100px;"></td>
				
				<td class="withinput"><fmt:message key="aimir.modem.type"/></td>
				<td colspan="2" class="padding-r20px">
					<select id="modemType" name="select" style="width:140px\9;width:136px">
						<option value=""><fmt:message key="aimir.all"/></option>
						<c:forEach var="modemType" items="${modemType}">
						<option value="${modemType.id}">${modemType.name}</option>
						</c:forEach>
					</select>
				</td>
				

				<td class="withinput"><fmt:message key="aimir.power.type"/></td>
				<td colspan="2" class="padding-r20px">
					<select id="powerType" name="select" style="width:140px\9;width:136px">
						<option value=""><fmt:message key="aimir.all"/></option>
						<c:forEach var="powerType" items="${powerType}">
						<option value="${powerType.id}">${powerType.name}</option>
						</c:forEach>
					</select>
				</td>
				<td class="withinput"><fmt:message key="aimir.meterlocation"/></td>
				<td class="padding-r20px">
					<input name="searchWord" id='searchWord' style="width:140px" type="text" />
					<input type="hidden" id="meterLocation" value=""></input>
				</td>
			  </tr>
			  <tr>
				<td class="withinput"><fmt:message key="aimir.batterystatus"/></td>
				<td class="padding-r20px">
					<select id="batteryStatus" name="select" style="width:100px;">
					<option value=0><fmt:message key="aimir.all"/></option>
					</select>
				</td>
				
				<td class="withinput"><fmt:message key="aimir.batteryvoltage"/></td>
				<td><select id="batteryVoltSign" name="select" style="width:40px;"></select></td>
				<td><input id="batteryVolt" type="text" style="width:80px;"></td>
				
				<td class="withinput"><fmt:message key="aimir.operating.day"/></td>
				<td><select id="operatingDaySign" name="select" style="width:40px;"></select></td>
				<td><input id="operatingDay" type="text" style="width:80px;"></td>

				<td colspan="2">
					<em class="am_button"><a href="javascript:send();" class="on"><fmt:message key="aimir.button.search" /></a></em>
				</td>
			</tr>
		</table>
		<div class="clear">
			<div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
				<div id="treeDivA"></div>
			</div>
		</div>
	</div>

</div>
<!--상단검색 끝-->



<div class="gadget_body clear">
	<div id="fcPieChartDiv" class="width-auto textalign-center">
		<ul>
			<li class="display_inline" id="fcLeftChart">The chart will appear within this DIV. This text will be replaced by the chart.</li>
			<li class="display_inline" id="fcRightChart">The chart will appear within this DIV. This text will be replaced by the chart.</li>
		</ul>
	</div>
	<div class="clear">
        <div id="batteryVoltageDiv"></div>
	</div>
</div>

<!-- 탭 구조 (S) -->
<div id="batteryLogTab">
    <ul>
       <li><a href="#log" id="_log">Battery Log</a></li>
    </ul>


    <!-- 1st 탭 : VoltageLevels (S) -->
    <div id="log" class="tabcontentsbox">
    <ul><li>

        <!-- search-default (S) -->
        <div class="blueline" style="height:360px;">

            <div class="search-bg-withouttabs margin-reset height-withouttabs-dayoptions-bt-row0">
                <div class="dayoptions-bt searchoption-container">
                   <%@ include file="/gadget/commonDateTabButtonType.jsp"%>
                </div>
            </div>

            <ul class="width" style="padding-top:0;">
            <li class="padding">

                <div class="gadget_body">
                    <div id="fcLogChart" class="floatleft width-50">
                        The chart will appear within this DIV. This text will be replaced by the chart.
                    </div>
                    <div class="floatleft width-49">
                        <div id ="batteryLogDetailDiv"></div>
                    </div>
                  </div>
            </li>
            </ul>
        </div>
        <!-- search-default (E) -->

    </li></ul>
    </div>
    <!-- 1st 탭 : VoltageLevels (E) -->


</div>
<!-- 탭 구조 (E) -->



</body>
</html>

