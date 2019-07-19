<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Billing Max Gadget</title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css" />
	<link href="${ctx}/js/extjs/resources/css/treegrid.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
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
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/src/widgets/grid/GridPanel.js"></script>
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
	<script type="text/javascript">

	    var supplierId = ${supplierId};

	    var tabs     = {hourly:0,daily:1,period:0,weekly:0,monthly:1,monthlyPeriod:0,weekDaily:0,seasonal:0,yearly:0};
	    var tabNames = {hourly:'',daily:'',period:'',weekly:'',monthly:'',monthlyPeriod:'',weekDaily:'',seasonal:'',yearly:''};

	    var fcChartDataXml;
	    var fcChart;
	    var chromeColAdd = 2;
	    
        //윈도우 리싸이즈시 event
          $(window).resize(function() {
                
                fcChartRender();
                //리싸이즈시 패널 인스턴스 kill & reload
                locationMaxGrid.destroy();
                locationMaxGridOn = false;
                
                getlocationMaxGrid();
                
          });
	    //검색버튼 클릭 이벤트
	    function send () 
	    {
			
	    	//파이 차트 업데이트
	        updateFChart();
	    	getlocationMaxGrid();
		};

		var customerSend = function() {
			 getcustomerMaxGrid();
		};

		$(function() {
			Ext.QuickTips.init();
			locationTreeGoGo('treeDivA', 'searchWord', 'locationId', 'location');
			locationTreeGoGo('customerTreeDivA', 'customerSearchWord', 'customerLocationId', 'customerLocation');

			$('#tariffIndex').selectbox();

			updateFChart();
			getlocationMaxGrid();
		});

	    var chartType = 'columnChart';

	   

	    var changeChart = function(_chartType) {

		    chartType = _chartType;

		    $('#columnChartBtn').removeClass();
		    $('#lineChartBtn').removeClass();

		    if(chartType == 'columnChart') {
		    	$('#columnChartBtn').addClass('current');
		    } else if(chartType == 'lineChart') {
		    	$('#lineChartBtn').addClass('current');
		    }
		    send();
	    };

	    var changeTab = function(_divName) {

	    	$('#billingLocDiv').hide();
	    	$('#billingCustomerDiv').hide();
	    	$('#locationTab').removeClass('current');
	    	$('#customerTab').removeClass('current');

	    	if(_divName == 'billingLocDiv') {
	    		$('#billingLocDiv').show();
	    		$('#locationTab').addClass('current');
	    	} else if(_divName == 'billingCustomerDiv') {
	    		$('#billingCustomerDiv').show();
	    		$('#customerTab').addClass('current');
	    		getcustomerMaxGrid();
	    	}
	    };

		//JS FUNC (파이 차트 draw event )
        function updateFChart() {
	    	// emergePre();
	    
	   	    $.getJSON('${ctx}/gadget/mvm/getBillingFusionChartData.do'
	   	    	    ,{startDate:$('#searchStartDate').val(), 
	   	    	    	endDate:$('#searchEndDate').val(),
	   	    	    	chartType:chartType, 
	   	    	    	searchDateType:$('#searchDateType').val(),
	   	    	    	locationIds:$('#locationId').val(), 
	   	    	    	serviceType:"HM"}
					,function(json) {
						var list = json.gridDatas;
                        fcChartDataXml = "<chart "
                        	+ "chartLeftMargin='0' "
						 	+ "chartRightMargin='0' "
						 	+ "chartTopMargin='20' "
						 	+ "chartBottomMargin='0' "
                       	 	+ "showValues='0' "
                       	 	+ "numberSuffix=' kWh' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_Column3D_nobg
                            + ">";
                   	 	var labels = "";
                        for( var index =0; index<list.length;index++){
	                         if(index != "indexOf") {
	                         	labels	+= "<set label='"+list[index].locationName+"' value='"+list[index].kwh+"'  color='"+fChartColor_HEAT[0]+"' />";
	                         }
                        }

                        if(list.length == 0) {
                       	 	labels	= "<set label=' ' value='0' color='"+fChartColor_HEAT[0]+"' />";
                        }
                        
                        fcChartDataXml += labels + "</chart>";
                        
                        fcChartRender();
	                }
	   	    );

	   		// hide();
		}

	    function fcChartRender() {
	    	if($('#fcChartDiv').is(':visible')) {
		    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Column3D.swf", "myChartId", $('#fcChartDiv').width(), "220", "0", "0");
		        fcChart.setDataXML(fcChartDataXml);
		        fcChart.setTransparent("transparent");
		        fcChart.render("fcChartDiv");
	    	}
	    }


	     var getLocationConditionArray = function() {

	        var arrayObj = Array();

	        arrayObj[0] = $('#searchStartDate').val();
	        arrayObj[1] = $('#searchEndDate').val();
			arrayObj[2] =  $('#searchDateType').val();
	        arrayObj[3] = chartType;
	        arrayObj[4] = $('#locationId').val();
	        arrayObj[5] = 'HM';

	        return arrayObj;
	    };

        var getLocationFmtMessage = function() {

            var fmtMessage = Array();

            fmtMessage[0] = "<fmt:message key="aimir.location"/>";
            fmtMessage[1] = "<fmt:message key="aimir.customercount"/>";
            fmtMessage[2] = "<fmt:message key="aimir.energy.kwh"/>";
            fmtMessage[3] = "<fmt:message key="aimir.criticalpeak"/>";
            fmtMessage[4] = "<fmt:message key="aimir.estimateCharge"/>";
            return fmtMessage;
        };

	    var locationMaxGridStore;
        var locationMaxGridColModel;
        var locationTreeRootNode;
        var locationMaxGridOn = false;
        var locationMaxGrid;
        //locationMax 그리드
        function getlocationMaxGrid(){

            var arrayObj = getLocationConditionArray();
            var message  = getLocationFmtMessage();
        	var width = $("#locationGridDiv").width(); 

             locationMaxGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 10}},
                url: "${ctx}/gadget/mvm/getLocationBillingGridData.do",
                baseParams:{

                    startDate    	: arrayObj[0],
                    endDate    		: arrayObj[1],
                    searchDateType  : arrayObj[2],
                    locationIds  	: arrayObj[4],
                    serviceType     : arrayObj[5],
                    pageSize        : 10
                },
                totalProperty: 'total',
                reader: new Ext.data.JsonReader({
	               
	                root:'gridDatas',
	                 fields: [
	                { name: 'locationName', type: 'String' },
	                { name: 'customerCnt', type: 'String' },
	                { name: 'usage', type: 'String' },
	                { name: 'maxUsage', type: 'String' }
	                /* , { name: 'usageCharge', type: 'String' } */
                ]}),
                root:'gridDatas',
                 fields: [
                { name: 'locationName', type: 'String' },
                { name: 'customerCnt', type: 'String' },
                { name: 'usage', type: 'String' },
                { name: 'maxUsage', type: 'String' }
                /* ,{ name: 'usageCharge', type: 'String' } */
                ],
                listeners: {
                	beforeload: function(store, options){
               		options.params || (options.params = {});
                	Ext.apply(options.params, {
                              page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)-1
                         });
                	},
                	load: function(store, record, options){
                    	makeLocGridTree();
               		}
                }
            });
			
        };

        function makeLocGridTree(){

        	 var message  = getLocationFmtMessage();
        	 var width = $("#locationGridDiv").width(); 
        	 locationMaxGridColModel = [

                    {
                        header:message[0],
                        tooltip:message[0],
                        dataIndex:'locationName',
                        width: width/4-5,
                        align:'center'
                     }
                     ,{
                        header:message[1],
                        tooltip:message[1],
                        dataIndex:'customerCnt',
                        width: width/4-5,
                        align:'center'
                        
                    }
                    ,{
                        header:message[2],
                        tooltip:message[2],
                        dataIndex:'usage',
                        width: width/4-5,
                        align:'center'
                    }
                    ,{
                        header:message[3],
                        tooltip:message[3],
                        dataIndex:'maxUsage',
                        width: width/4-5,
                        align:'center'
                    }
                    /* ,{
                        header:message[4],
                        tooltip:message[4],
                        dataIndex:'usageCharge',
                        width: width/5-5,
                        align:'center'
                    } */
                ];

			locationTreeRootNode = new Ext.tree.AsyncTreeNode({
	            text: 'root',
	            id: 'gridDatas',
	            allowChildren: true,
	            draggable:true,
	            expended:false,

	            children: locationMaxGridStore.reader.jsonData.gridDatas
        	});

				if (!locationMaxGridOn) {
	            	locationMaxGrid = new Ext.ux.tree.TreeGrid({
	                width: width,
	                height: 525,
	                useArrows: true,
	                renderTo: "locationGridDiv",
	                enableDD: true,
	                columns: locationMaxGridColModel,
	                root: locationTreeRootNode,
	                bbar: new Ext.PagingToolbar({
                        pageSize: 10,
                        store: locationMaxGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'                
                    })
	            });

		            locationMaxGridOn = true;
		        } else {
		            locationMaxGrid.setWidth(width);
		            locationMaxGrid.setRootNode(locationTreeRootNode);
		            var bottomToolbar = locationMaxGrid.getBottomToolbar();
		            bottomToolbar.bindStore(locationMaxGridStore);
		            locationMaxGrid.render();
		        }
        };

		 var locationName;
		 var customerCnt;
		 var usage;
		 var maxUsage;
		 var usageCharge;

		 function receiveMsg(value) {
	        if (value.locationName == undefined) {
	            locationName = -1;
	        } else {
	            locationName = value.locationName;
	        }

	        customerCnt = value.customerCnt;
	        usage = value.usage;

	        maxUsage = value.maxUsage;
	        usageCharge = value.usageCharge;

	        loadCustomerInfo();
    	}
	    var getCustomerConditionArray = function() {

	        var arrayObj = Array();

	        arrayObj[0] = $('#searchStartDate').val();
	        arrayObj[1] = $('#searchEndDate').val();
			arrayObj[2] = $('#searchDateType').val();
			arrayObj[3] = $('#customerLocationId').val();
			arrayObj[4] = $('#tariffIndex').val();
			arrayObj[5] = $('#customerName').val();
			arrayObj[6] = $('#contractNo').val();
			arrayObj[7] = $('#meterName').val();
			arrayObj[8] = 'HM';
			arrayObj[9] = supplierId;
	        return arrayObj;
	    };

        var getcustomerFmtMessage = function() {

            var fmtMessage = Array();

            fmtMessage[0] = "<fmt:message key="aimir.number"/>";
            fmtMessage[1] = "<fmt:message key="aimir.date"/>";
            fmtMessage[2] = "<fmt:message key="aimir.customername"/>";
            fmtMessage[3] = "<fmt:message key="aimir.contractNumber"/>";
            fmtMessage[4] = "<fmt:message key="aimir.meterid"/>";
            fmtMessage[5] = "<fmt:message key="aimir.energy.kwh"/>";
            fmtMessage[6] = "<fmt:message key="aimir.mem.power_search.PreviousBilling.desc14"/>" + " <fmt:message key="aimir.indicator"/>" + "(KWh)";
            fmtMessage[7] = "<fmt:message key="aimir.criticalpeak"/>";
            fmtMessage[8] = "<fmt:message key="aimir.estimateCharge"/>";
            fmtMessage[9] = "<fmt:message key="aimir.excel.elecBillingHm"/>";


            return fmtMessage;
        };

        var win;
        var excel = function() {

        	var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no";
            var obj = new Object();
            var fmtMessage1 = new Array();
            var condition1  = new Array();

            fmtMessage1 = getcustomerFmtMessage();
            condition1 = getCustomerConditionArray();

            obj.condition = condition1;
            obj.fmtMessage = fmtMessage1;
            obj.url = '${ctx}/gadget/mvm/billingMaxExcelMake.do';

            if(win)
                win.close();
            win = window.open(
                            "${ctx}/gadget/ExcelDownloadPopup.do",
                            "HeatBillingExcel", opts);
            win.opener.obj = obj;
        };

	  	//컬럼 Tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }

	    var customerMaxGridStore;
        var customerMaxGridColModel;
        var customerMaxGridOn = false;
        var customerMaxGrid;
        //customerMax 그리드
        function getcustomerMaxGrid(){

            var arrayObj = getCustomerConditionArray();
            var message  = getcustomerFmtMessage();

            var width = $("#customerGridDiv").width(); 

             customerMaxGridStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 20}},
                url: "${ctx}/gadget/mvm/getCustomerBillingGridData.do",
                baseParams:{

                    startDate    	: arrayObj[0],
                    endDate    		: arrayObj[1],
                    searchDateType  : arrayObj[2],
                    locationIds  	: arrayObj[3],
                    tariffIndex     : arrayObj[4],
                    customerName    : arrayObj[5],
                    contractNo      : arrayObj[6],
                    meterName       : arrayObj[7],
                    serviceType     : arrayObj[8],
                    supplierId      : arrayObj[9],
                    pageSize        : 20
                },
                totalProperty: 'total',
                root:'gridDatas',
                 fields: [
                { name: 'rownum', type: 'Integer' },
                { name: 'yyyymmdd', type: 'String' },
                { name: 'customerName', type: 'String' },
                { name: 'contractNo', type: 'String' },
                { name: 'meterName', type: 'String' },
                { name: 'total', type: 'String' },
                { name: 'usage', type: 'String' },
                { name: 'max', type: 'String' }
                /* ,{ name: 'usageCharge', type: 'String' } */
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

            customerMaxGridColModel = new Ext.grid.ColumnModel({
               
                columns: [

                    {
                        header:message[0],
                        tooltip:message[0],
                        dataIndex:'rownum',
                        width: 5 ,
                        align:'center'
                     }
                     ,{
                        header:message[1],
                        tooltip:message[1],
                        dataIndex:'yyyymmdd',
                        width: 10,
                        align:'center'
                        
                    }
                    ,{
                        header:message[2],
                        tooltip:message[2],
                        dataIndex:'customerName',
                        width: 10 ,
                        align:'center'
                    }
                    ,{
                        header:message[3],
                        tooltip:message[3],
                        dataIndex:'contractNo',
                        width: 10 ,
                        align:'center'
                    }
                    ,{
                        header:message[4],
                        tooltip:message[4],
                        dataIndex:'meterName',
                        width: 10 ,
                        align:'center'
                    }
                    ,{
                        header:message[5],
                        tooltip:message[5],
                        dataIndex:'total',
                        width: 10 ,
                        align:'center'
                    }
                   
                    ,{
                        header:message[6],
                        tooltip:message[6],
                        dataIndex:'usage',
                        width: 10 ,
                        align:'center'
                    }
                     ,{
                        header:message[7],
                        tooltip:message[7],
                        dataIndex:'max',
                        width: 10 ,
                        align:'center'
                    }
                    /*  ,{
                        header:message[8],
                        tooltip:message[8],
                        dataIndex:'usageCharge',
                        width: 10 ,
                        align:'center'
                    } */
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: ((width-30)/4)-chromeColAdd
                    ,renderer: addTooltip
                }
            });

            if (customerMaxGridOn == false) {
               
                customerMaxGrid = new Ext.grid.GridPanel({
                   
                    id: 'customerMaxGrid',
                    store: customerMaxGridStore,
                    cm : customerMaxGridColModel,
                    autoScroll: true,
                    width: width,
                    height: 525,
                    stripeRows : true,
                    columnLines: true,
                    loadMask: {
                        msg: 'loading...'
                    },
                    renderTo: 'customerGridDiv',
                    viewConfig: {
                       
                        forceFit:true,
                         scrollOffset: 1,
                         enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                     bbar: new Ext.PagingToolbar({
                        pageSize: 20,
                        store: customerMaxGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'                
                    })
                });
               
                customerMaxGridOn  = true;

            } else {
                
                customerMaxGrid.setWidth(width);
                customerMaxGrid.reconfigure(customerMaxGridStore, customerMaxGridColModel);
                 var bottomToolbar = customerMaxGrid.getBottomToolbar();
                bottomToolbar.bindStore(customerMaxGridStore);
            }
            
        };
	</script>

</head>

<body>



<!--상단탭-->
<div id="gad_sub_tab">
	<ul>
		<li><a href="javascript:changeTab('billingLocDiv')" id="locationTab" class="current"><fmt:message key="aimir.location"/></a></li>
		<li><a href="javascript:changeTab('billingCustomerDiv')" id="customerTab"><fmt:message key="aimir.search.customer"/></a></li>
	</ul>
</div>

<input type='hidden' id='locationId' value=''></input>
<input type='hidden' id='customerLocationId' value=''></input>

<!-- search-background DIV (S) -->
<div class="search-bg-withouttabs with-dayoptions-bt height-withouttabs-dayoptions-bt-row2">

	<div class="dayoptions-bt">
	<%@ include file="/gadget/commonDateTabButtonType.jsp"%>
	</div>

	<div class="dashedline"></div>


			<!-- Tab1 : location DIV (S) -->
			<div id="billingLocDiv">

				<div class="searchoption-container">
					<table class="searchoption wfree">
						<tr>
							<td>
								<input name="searchWord" id='searchWord' class="billing-searchword" type="text" value='<fmt:message key="aimir.board.location"/>' />
							</td>
							<td>
								<em class="am_button"><a href="javascript:send();" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></em>
							</td>
						</tr>
					</table>
				</div>

				<div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
					<div id="treeDivA"></div>
				</div>


				<!-- gadget buttons (E) -->
				<div class="height20px clear"></div>
				<div id="fcChartDiv" class="gadget_body"></div>

				<div id="locationGridDiv" class="gadget_body">
				</div>

			</div>
			<!-- Tab1 : location DIV (E) -->



			<!-- Tab2 : customer DIV (S) -->
			<div id="billingCustomerDiv" style="display:none">

				<div class="searchoption-container">
					<table class="searchoption wfree">
						<tr>
							<td colspan="2" class="padding-r20px">
							<input name="customerSearchWord" id="customerSearchWord" class="billing-searchword" type="text" value="<fmt:message key="aimir.supplySelectArea"/>" /></td>
							<td class="withinput padding-r20px"><fmt:message key="aimir.contract.tariff.type"/></td>
							<td colspan="5">
								<select name="tariffIndex" id="tariffIndex" style="width:338px;width:332px\9;">
									<option value=""><fmt:message key="aimir.contract.tariff.type"/></option>
									<c:forEach var="tariffType" items="${tariffTypes}">
										<option value="${tariffType.id}">${tariffType.name}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<td class="withinput"><fmt:message key="aimir.customername"/></td>
							<td class="padding-r20px"><input id="customerName" type="text"/></td>
							<td class="withinput"><fmt:message key="aimir.contractNumber"/></td>
							<td class="padding-r20px"><input id="contractNo" type="text" /></td>
							<td class="withinput"><fmt:message key="aimir.meterid" /></td>
							<td><input id="meterName" type="text" /></td>
							<td>
								<em class="am_button"><a href="javascript:customerSend();" class="on" id="btnSearch"><fmt:message key="aimir.button.search" /></a></em>
							</td>
						</tr>
					</table>
				</div>


				<div id="customerTreeDivAOuter" class="tree-billing auto" style="display:none;">
					<div id="customerTreeDivA"></div>
				</div>

				<div class="height20px clear"></div>
				<div class="btn_right_top2 padding-r2">
					<em class="am_button"><a href="javaScript:excel();"><fmt:message key="aimir.button.excel" /></a></em>
				</div>

				<div id="customerGridDiv" class="gadget_body2"></div>

			</div>
			<!-- Tab2 : customer DIV (E) -->


</div>
<!-- search-background DIV (E) -->


</body>
</html>