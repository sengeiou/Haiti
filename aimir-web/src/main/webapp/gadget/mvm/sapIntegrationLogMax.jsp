<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>

<%@ include file="/gadget/system/preLoading.jsp"%>

<style type="text/css">
        /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
        .x-toolbar-left {
            width:223px !important;
        }
        .x-toolbar-right {
            position: absolute; right: 0px;
        }
        /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
         @media screen and (-webkit-min-device-pixel-ratio:0) { 
            .x-grid3-row td.x-grid3-cell {
                padding-left: 0px;
                padding-right: 0px;
            }
        } 
        
</style>

<script type="text/javascript" charset="utf-8">
	
	//탭초기화
	// 값 0 - 숨김처리
	// daily-일자별,period-기간별,weekly-주별,monthly-월별,weekDaily-요일별,seasonal-계절별
	var tabs     = {hourly:0,daily:0,period:1,weekly:0,monthly:0,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
	
	// 탭명칭 변경시 값입력
	var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};
	var startDate;
	var endDate;

	//outbound Log 그리드 관련 변수
	var renderOutboundLogGrid;
	
 	var outboundLogStore;
	var outboundLogGrid;
	var outboundLogGridOn = false;
	
	//inbound Log 그리드 관련 변수
	var renderInboundLogGrid;
	
	var inboundLogStore;
	var inboundLogGrid;
	var inboundLogGridOn = false;
	
	var inboundLogTotalGrid;
	var inboundLogTotalGridOn = false;
	
	var renderInboundLogTotalGrid;
	
	var inboundTotalCnt_total = 0;
	var inboundMeterCnt_total = 0;
	
	//error Log 그리드 관련 변수
	var renderErrorLogGrid; 
	
 	var errorLogStore;
	var errorLogGrid;
	var errorLogGridOn = false;
	
	var errorLogTotalGridOn = false;
	
	var errorTotalCnt = 0;
	
	//outBound Log Grid Function
	renderOutboundLogGrid = function() {
		
		//outBound Log's Store
		outboundLogStore = new Ext.data.JsonStore({
		autoLoad		: {params:{start: 0, limit: 30}},
		url		 		: '${ctx}/gadget/mvm/getOutBoundGridData.do',
		baseParams		: {	startDate	: $("#searchStartDate").val()+"000000",
							endDate		: $("#searchEndDate").val()+"235959"
		},
	       root			: 'outGridData',
	       totalProperty	: 'outGridTotalCnt',
	       //idProperty	: "NO",
	       listeners		: {
	       	beforeload	: function(store, options){
	               Ext.apply(options.params, {
	               	page: Math.ceil((options.params.start + options.params.limit) / options.params.limit) - 1
	               });
	              }
	          },
	       fields	: [
				{ name: 'NO', type: 'integer' },
				{ name: 'OUTBOUND_FILENAME', type: 'String' },
	          	{ name: 'OUTBOUND_DATE', type: 'string' },
	          	{ name: 'OUTBOUND_TOTALCNT', type: 'integer' },
	          	{ name: 'OUTBOUND_METERCNT', type: 'integer' }
	       ]		        
	   });
	
		//outBound Log's Model
		var outboundLogModel = new Ext.grid.ColumnModel({
			defaults : {
				sortable: true,
				menuDisabled : true,
				align : "center"
			}, columns : [{
				header : "<b><fmt:message key='aimir.number'/></b>",
				dataIndex : "NO",
				width : 1
			}, {
				header : "<b><fmt:message key='aimir.sap.outbound.filename'/></b>",
				dataIndex : "OUTBOUND_FILENAME",
				width : 7
			},{
				header : "<b><fmt:message key='aimir.sap.outbound.date'/></b>",
				dataIndex : "OUTBOUND_DATE",
				width : 5
			},{
				header : "<b><fmt:message key='aimir.mvm.totalCount'/></b>",
				dataIndex : "OUTBOUND_TOTALCNT",
				width : 3
			},{
				header : "<b><fmt:message key='aimir.sap.meterCount'/></b>",
				dataIndex : "OUTBOUND_METERCNT",
				width : 3
			}]
		});
			
		//outBound Log's Grid
		if(!outboundLogGridOn) {
			outboundLogGrid = new Ext.grid.GridPanel({		
			   layout : 'fit',	 			
		   height		: 660,
		   width 		: $("#outBoundGrid").width(),
	       store		: outboundLogStore,
	       colModel 	: outboundLogModel,
	       stripeRows 	: true,
	       columnLines	: true,
	       loadMask		:{
	           msg: 'loading...'
	       },            
	       renderTo		: 'outBoundGrid',
	       viewConfig	: {
	    	   forceFit			:true,
	           enableRowBody	:true,
	           showPreview		:true,
	           emptyText		: 'No data to display'
	       },
	       listeners: {
	    	   		//outbount Log의 record 클릭시 inbound Log와 error log를 검색해서 결과를 보옂줌
                   rowclick: outBoundRowClickEvent
               },
	    	// paging bar on the bottom
	       bbar : new Ext.PagingToolbar({
	           pageSize			: 30,
	           store			: outboundLogStore,
	           displayInfo		: true,
	           displayMsg		: ' {0} - {1} / {2}'
	       })
	   });
			outboundLogGridOn = true;
		} else {
			var bottomToolbar = outboundLogGrid.getBottomToolbar();
			outboundLogGrid.reconfigure(outboundLogStore, outboundLogModel);
			bottomToolbar.bindStore(outboundLogStore);
		}
	};
	
	//inBound Log Grid Function
	renderInboundLogGrid = function(outboundFileName, outboundDate) {
	
		//inBound Log's Store
		inboundLogStore = new Ext.data.JsonStore({
			autoLoad 		: true,
			url		 		: '${ctx}/gadget/mvm/getInBoundGridData.do',
	        baseParams		: {	outboundFileName	:	outboundFileName,
	        					outboundDate		:	outboundDate
	        },
			root			: 'inGridData',
			idProperty		: "NO",
			listeners		: {
				load	:	function(store) {
					inboundTotalCnt_total = store.reader.jsonData.inboundTotalCnt_Total;
					inboundMeterCnt_total = store.reader.jsonData.inboundMeterCnt_Total;
					renderInboundLogTotalGrid();
				}
			}, 
	        fields			: [
				{ name: 'NO', type: 'integer' },
				{ name: 'INBOUND_FILENAME', type: 'String' },
	           	{ name: 'INBOUND_DATE', type: 'string' },
	           	{ name: 'INBOUND_TOTALCNT', type: 'integer' },
	           	{ name: 'INBOUND_METERCNT', type: 'integer' },
	        ]		        
	    });

		
		//inBound Log's Model
 		var inboundLogModel = new Ext.grid.ColumnModel({
 			defaults : {
 				menuDisabled : true,
 				align : "center",
 			}, columns : [{
				header : "<b><fmt:message key='aimir.number'/></b>",
				dataIndex : "NO",
				width : $("#inBoundGrid").width()*0.05
			},{
 				header : "<b><fmt:message key='aimir.sap.inbound.filename'/></b>",
 				dataIndex : "INBOUND_FILENAME",
 				width : $("#inBoundGrid").width()*0.36
 			},{
 				header : "<b><fmt:message key='aimir.sap.inbound.date'/></b>",
 				dataIndex : "INBOUND_DATE",
 				width : $("#inBoundGrid").width()*0.27,
	   			fixed : true
 			},{
 				header : "<b><fmt:message key='aimir.mvm.totalCount'/></b>",
 				dataIndex : "INBOUND_TOTALCNT",
 				width : $("#inBoundGrid").width()*0.15,
	   			fixed : true
 			},{
 				header : "<b><fmt:message key='aimir.sap.meterCount'/></b>",
 				dataIndex : "INBOUND_METERCNT",
 				width : $("#inBoundGrid").width()*0.15,
	   			fixed : true
 			}]
 		});
 		
 		//inBound Log's Grid
 		if(!inboundLogGridOn) {
 			inboundLogGrid = new Ext.grid.GridPanel({		
 			   layout 		: 'fit',	 
			   height		: 300,
			   width 		: $("#inBoundGrid").width(),
		       store		: inboundLogStore,
		       colModel 	: inboundLogModel,
		       stripeRows 	: true,
		       columnLines	: true,
		       loadMask		:{
		           msg: 'loading...'
		       },            
		       renderTo		: 'inBoundGrid',
		       viewConfig	: {
		    	   forceFit			:true,
		           enableRowBody	:true,
		           showPreview		:true,
		           emptyText		: 'No data to display'
		       }
		   });
 			inboundLogGridOn = true;
		} else {
			inboundLogGrid.reconfigure(inboundLogStore, inboundLogModel);
		}
 		
	};
	
	//inBound Log's Total Grid Function
	renderInboundLogTotalGrid = function () {
		
		//inBound Log's Grid 의 Total Count와 Meter Count 의 합계를 나타낸 inBoundLog Total's Model
     	var inboundLogTotalModel = new Ext.grid.ColumnModel({
  			columns : [{
  				header : "<b><fmt:message key='aimir.total'/></b>",
	   			width : $("#inBoundGrid").width()*0.7,
	   			menuDisabled : true,
	   			align : "center",
	   			fixed : true
  			},{
				header : "<b>"+inboundTotalCnt_total+"</b>",
				width : $("#inBoundGrid").width()*0.15,
				menuDisabled : true,
   			align : "center",
   			fixed : true
			},{
				header : "<b>"+inboundMeterCnt_total+"</b>",
				width : $("#inBoundGrid").width()*0.15,
				menuDisabled : true,
   			align : "center",
   			fixed : true
			}]
  		});
		 
		//inBound Log's Grid 의 Total Count와 Meter Count 의 합계를 나타낸 inBoundLog Total's Grid
		if(!inboundLogTotalGridOn) {
		   inboundLogTotalGrid = new Ext.grid.GridPanel({		
		   layout 		: 'fit',	 
		   height		: 27,
		   width 		: $("#inBoundTotalGrid").width(),
	       store		: inboundLogStore,
	       colModel 	: inboundLogTotalModel,      
	       renderTo		: 'inBoundTotalGrid',
	       viewConfig	: {
	    	   forceFit			:true,
	           enableRowBody	:true,
	           showPreview		:true,
	           emptyText		: 'No data to display'
	       }
	   });
			inboundLogTotalGridOn = true;
		} else {
			inboundLogTotalGrid.reconfigure(inboundLogStore, inboundLogTotalModel);
		}
	};
		
	//error Log Grid Function
	renderErrorLogGrid = function(outboundFileName, outboundDate) {
		
		//error Log's Store
		errorLogStore = new Ext.data.JsonStore({
		autoLoad : true,
		url		 : '${ctx}/gadget/mvm/getErrorLogGridData.do',
		baseParams		: {	outboundFileName	:	outboundFileName,
							outboundDate		:	outboundDate
		},
		root			: 'errorGridData',
		idProperty		: "NO",
		listeners		: {
			load	:	function(store) {
				errorTotalCnt = store.reader.jsonData.errorGridTotalCnt;
				renderErrorLogTotalGrid();
			}
		},
	    fields	: [
			{ name: 'NO', type: 'integer' },
			{ name: 'METER_SERIAL', type: 'String' },
	        { name: 'ERROR_REASON', type: 'string' },
	     ]		        
	   });
		
		//error Log's Model
		var errorLogModel = new Ext.grid.ColumnModel({
			defaults : {
				menuDisabled : true,
				align : "center"
			}, columns : [{
				header : "<b><fmt:message key='aimir.number'/></b>",
				dataIndex : "NO",
				width : $("#errorLogGrid").width()*0.07
			},{
				header : "<b><fmt:message key='aimir.sap.meterSerial'/></b>",
				dataIndex : "METER_SERIAL",
				width : $("#errorLogGrid").width()*0.38
			},{
				header : "<b><fmt:message key='aimir.sap.errorReason'/></b>",
				dataIndex : "ERROR_REASON",
				width : $("#errorLogGrid").width()*0.65,
				fixed : true
			}]
		});
	
		//error Log's Grid
		if(!errorLogGridOn) {
			errorLogGrid = new Ext.grid.GridPanel({	
			   layout : 'fit',	 
			   height		: 269,
			   width 		: $("#errorLogGrid").width(),
		       store		: errorLogStore,
		       colModel 	: errorLogModel,
		       stripeRows 	: true,
		       columnLines	: true,
		       loadMask		:{
		           msg: 'loading...'
		       },            
		       renderTo		: 'errorLogGrid',
		       viewConfig	: {
		    	   forceFit			:true,
		           enableRowBody	:true,
		           showPreview		:true,
		           emptyText		: 'No data to display'
		       }
		   	});
				errorLogGridOn = true;
		} else {
			errorLogGrid.reconfigure(errorLogStore, errorLogModel);
		}
		
	};  
	
	//error Log's Total Grid Function
	var renderErrorLogTotalGrid = function () {

	//error Log's Grid 의 record 갯수를 나타낸 error Log Total's Model
	var errorLogTotalModel = new Ext.grid.ColumnModel({
  			columns : [{
  				header : "<b><fmt:message key='aimir.total'/></b>",
	   			width : $("#inBoundTotalGrid").width()*0.35,
	   			menuDisabled : true,
	   			align : "center",
	   			fixed : true
  			},{
				header : "<b>"+errorTotalCnt+"</b>",
				width : $("#inBoundTotalGrid").width()*0.65,
				menuDisabled : true,
   			align : "center",
   			fixed : true
			}]
  		});
		
		//error Log's Grid 의 record 갯수를 나타낸 error Log Total's Grid
		if(!errorLogTotalGridOn) {
			errorLogTotalGrid = new Ext.grid.GridPanel({		
		  	 layout 	: 'fit',
		  	 height		: 27,
		  	 width 		: $("#inBoundTotalGrid").width(),
	      	 store		: errorLogStore,
	      	 colModel 	: errorLogTotalModel,         
	     	 renderTo	: 'errorLogTotal',
	     	 viewConfig	: {
	    		   forceFit			:true,
	       	  	   enableRowBody	:true,
	        	   showPreview		:true,
	        	   emptyText		: 'No data to display'
	      	 }
	  	 });
			errorLogTotalGridOn = true;
		} else {
			errorLogTotalGrid.reconfigure(errorLogStore, errorLogTotalModel);
		}
	};
	 	
 	$(document).ready(function() {
 		window.setTimeout(function () {renderOutboundLogGrid();}, 500);
 		renderInboundLogGrid("","");
 		renderErrorLogGrid("","");
 		renderInboundLogTotalGrid();
 		renderErrorLogTotalGrid();
 		hide();
 		
 	});
 	
 	function outBoundRowClickEvent(grid) {
 		var outboundFileName = grid.getSelectionModel().getSelected().json.OUTBOUND_FILENAME;
 		var outboundDate = grid.getSelectionModel().getSelected().json.OUTBOUND_DATE;
 		
 		renderInboundLogGrid(outboundFileName, outboundDate);
 		renderErrorLogGrid(outboundFileName, outboundDate);
 	}
 	
    function send(){
    	renderOutboundLogGrid();
	}
	

</script>
</head>
<body>
<!-- period 검색 부분 start -->
<div class="search-bg-withtabs">
	<div class="dayoptions">
		<%@ include file="/gadget/commonDateTab.jsp"%>
	</div>
</div>
<!-- period 검색 부분 end -->

<table>
	<tr>
		<!-- 왼쪽 그리드 : OutBound Log Grid Start-->
		<td style="width: 50%; padding: 10px; " rowspan="2">
			<label class="check"><fmt:message key='aimir.sap.outboundLog'/></label>
			<div id="outBoundGrid" style="padding-top: 5px"></div>
		</td>
		<!-- 왼쪽 그리드 : OutBound Log Grid End-->
		<!-- 오른쪽 상단 그리드 : Inbound Log Grid Start -->
		<td style="width: 50%; padding: 10px;">
			<label class="check"><fmt:message key='aimir.sap.inboundLog'/></label>
			<!-- InBound Log를 보여주는 Grid -->
			<div id="inBoundGrid"  style="padding-top: 5px"></div>
			<!-- InBound Log의 Tatol 값을 보여주는 Grid -->
			<div id="inBoundTotalGrid"></div>
		</td>
		<!-- 오른쪽 상단 그리드 : Inbound Log Grid End -->
	</tr>
	<tr>
		<!-- 오른쪽 하당 그리드 : error Log Grid Start -->
		<td style="width: 50%; padding: 10px;">
			<label class="check"><fmt:message key='aimir.sap.errorLog'/></label>
			<!-- error Log를 보여주는 Grid -->
			<div id="errorLogGrid" style="padding-top: 5px"></div>
			<!-- error Log의 Tatol 값을 보여주는 Grid -->
			<div id="errorLogTotal"></div>
		</td>
		<!-- 오른쪽 하당 그리드 : error Log Grid End-->
	</tr>
</table>

</body>
</html>