<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

    <title><fmt:message key="aimir.demand.management"/></title>

	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <!-- 스타일 추가 extjs css -->
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
    </style>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>   
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <%-- Ext-JS 관련 javascript 파일 두개 추가. 추후에 public.js 에 포함될 예정. --%>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

	    //탭초기화
        var tabs = {hourly:0,period:1,monthlyPeriod:0,yearly:0};
        var tabNames = {};

        var flex;
        var supplierId =${sesSupplierId};
        var serviceType = ServiceType.Electricity;

        var fcChart;
        var fcChartDataXml;        

        var fmtMessage1 = "<fmt:message key='aimir.offpeak'/>";				//off-peak
        var fmtMessage2 = "<fmt:message key='aimir.peak'/>";				//peak
        var fmtMessage3 = "<fmt:message key='aimir.criticalpeak'/>";		//critical peak
        
        /**
         * 유저 세션 정보 가져오기
         */
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    if(json.supplierId != ""){
                        supplierId = json.supplierId;
                    }
                }
        );
        
        var browserWidth="";

        $(document).ready(function(){
        	$.ajaxSetup({
                async: false
            });

            flex = getFlexObject('demandMgmtMaxChart');

            if(tabClickExec){$('#dailySearch').parent().css('display','none');};
            if(tabClickExec){$('#weeklySearch').parent().css('display','none');};
            if(tabClickExec){$('#monthlySearch').parent().css('display','none');};
            if(tabClickExec){$('#weekDailySearch').parent().css('display','none');};
            if(tabClickExec){$('#seasonalSearch').parent().css('display','none');};

            //조회버튼클릭 이벤트 생성
            $('#btnSearch').bind('click',function(event) {
            	
            	
            	//alert("btnSearch");
            	//alert($('#searchDateType').val());
            		
            	//데일리:1
           		sendRequest2($('#searchDateType').val()); } 
            	
            	
            	
           	); 
            
            function sendRequest2(_dateType){
            	
            	
            	//alert(_dateType);
                
                // 조회조건 검증
                if(!validateSearchCondition(_dateType))
               	{
                	//alert("false");
                	return false;
                }
                else
                {
                	//alert("날짜검증ok");
                	
                }
                

                //파이 차트 호출.
            	updateFChart();
            	
            	//플렉스 send func invoke
                //flex.requestSend('search');      
            	
            	
            	//그리드 차트 호출 (extjs grid)
            	getDemandMgmtMaxChartGrid();
            	
            	
            };

            
            

            locationTreeGoGo('treeeDiv', 'searchWord', 'locationId');
            getTariffTypeList();
            updateFChart();

            $.ajaxSetup({
                async: true
            });
            
            browserWidth= $(window).width();
            
            getDemandMgmtMaxChartGrid();
       	 
            
        });//document Ready End
        
        
    	
    	//윈도우 리싸이즈시 event
    	$(window).resize(function() {
        	  
    	    	browserWidth= $(window).width();   // returns width of browser viewport
    			//alert(browserWidth);
    	    	
    	    	//리싸이즈시 패널 인스턴스 kill & reload
    	    	demandMgmtMaxChartGridPanel.destroy();
    			
    	    	demandMgmtMaxChartGridInstanceOn = false;
    			
    	    	getDemandMgmtMaxChartGrid();
    	    	
    	});
    		
        
        
        //#######demandMgmtMaxChart Start
        
    	//demandMgmtMaxChartGrid propeties
        var demandMgmtMaxChartGridInstanceOn = false;
        var demandMgmtMaxChartGrid;
        var demandMgmtMaxChartColModel;
        var demandMgmtMaxChartCheckSelModel;

        

        function getDemandMgmtMaxChartGrid()
        {
        	
            //setting grid panel width
        	var gridWidth = (browserWidth-30);
        	
        	//row Count per page
            var rowSize = 10;
              
            var condArray = new Array();           
              
            condArray= getCondition2();
             
           
           //### demandMgmtMaxChartGrid Store fetch
            var demandMgmtMaxChartGridStore = new Ext.data.JsonStore({
            	
            	
                autoLoad: {params:{start: 0, limit: rowSize }},
                url: "${ctx}/gadget/mvm/getDemandManagementList.do", 
     
                
     
                //파라매터 설정.
                baseParams: {
                	
		                     meterType:condArray[0]  ,
			                supplierId :condArray[1]  ,
			                locationId :condArray[2] ,
			                tariffType :condArray[3] ,
			                dateType  :condArray[4] ,
			                startDate :condArray[5] ,
			                endDate  :condArray[6] ,
			                season   :   condArray[7] 
                }, 
             
               
                //Total Cnt
                totalProperty: "totalCnt",
                    
                
                root:'demandManagementGridList',
                
                
                fields: [
                         , "idx" 
                         , "location" 
                         , "tariffName"
                         , "offPeak"
                         , "peak"
                         , "criticalPeak"
                         , "avgPeak" 
                         , "loadFactor" 
                      
                         ],
                
                listeners: 
                {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });//Store End
            
            var fmtMessage = new Array();

            fmtMessage= getFmtMessage();
            
            
            // demandMgmtMaxChartGrid Model DEfine
            demandMgmtMaxChartGridModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: fmtMessage[0], dataIndex: 'idx', width: gridWidth/8,resizable: true}
                   ,{header: fmtMessage[1], dataIndex: 'location', width:gridWidth/8,resizable: true}
                   ,{header: fmtMessage[2], dataIndex: 'tariffName', width:gridWidth/8,resizable: true}
                   ,{header: fmtMessage[3], dataIndex: 'offPeak', width:gridWidth/8,resizable: true}
                   ,{header: fmtMessage[4], dataIndex: 'peak', width:gridWidth/8,resizable: true}
                   ,{header: fmtMessage[5], dataIndex: 'criticalPeak', width:gridWidth/8,resizable: true}
                   ,{header: fmtMessage[6], dataIndex: 'avgPeak', width:gridWidth/8,resizable: true}
                   ,{header: fmtMessage[7], dataIndex: 'loadFactor', width:(gridWidth/8)-10,resizable: true}
                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
               }
            
            });

            if(demandMgmtMaxChartGridInstanceOn == false)
            {
     
            	//그리고 패널 정의
               demandMgmtMaxChartGridPanel = new Ext.grid.GridPanel({
                    //title: '최근 한달 Demand Response History',
                    store: demandMgmtMaxChartGridStore,
                    colModel : demandMgmtMaxChartGridModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    scroll:false,
                    width:  gridWidth,
                    style: 'align:center;',
                    //패널 높이 설정
                    height: 305,
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    renderTo: 'demandMgmtMaxChartGridDiv',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    }/* ,
                    // paging bar on the bottom
                    bbar: new Ext.PagingToolbar({
                        pageSize: rowSize,
                        store: demandMgmtMaxChartGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    }) */
                });
                demandMgmtMaxChartGridInstanceOn = true;
             }
            else 
            {
            
            	//alert("reload");
            	
            	demandMgmtMaxChartGridPanel.setWidth( gridWidth);
                //var bottomToolbar = demandMgmtMaxChartGridPanel.getBottomToolbar();
                demandMgmtMaxChartGridPanel.reconfigure(demandMgmtMaxChartGridStore, demandMgmtMaxChartGridModel);
                //bottomToolbar.bindStore(demandMgmtMaxChartGridStore);
            } 
            
            hide();
            
        };//func demandMgmtMaxChartGridList End
        
        
        
        
        
        
        

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

      
        function getFmtMessage(){
            var cnt = 0;
            var fmtMessage = new Array();

            fmtMessage[cnt++] = "<fmt:message key='aimir.number'/>";                // 번호
            fmtMessage[cnt++] = "<fmt:message key='aimir.location.supplier'/>";     // 공급지역
            fmtMessage[cnt++] = "<fmt:message key='aimir.contract.tariff.type'/>";  // 계약종별
            fmtMessage[cnt++] = "<fmt:message key='aimir.offpeak'/>";               // 경부하
            fmtMessage[cnt++] = "<fmt:message key='aimir.peak'/>";                  // 중간부하
            fmtMessage[cnt++] = "<fmt:message key='aimir.criticalpeak'/>";          // 최대부하
            fmtMessage[cnt++] = "<fmt:message key='aimir.peak.average'/>";          // 부하 평균
            fmtMessage[cnt++] = "<fmt:message key='aimir.load.factor'/>";           // load factor
            fmtMessage[9] = "<fmt:message key="aimir.alert"/>";

            fmtMessage[10] = "<fmt:message key='aimir.firmware.msg09'/>"; 		//데이터를 찾을수 없습니다.
            fmtMessage[11] = "<fmt:message key='aimir.excel.demandmgmtMax'/>"; 

            return fmtMessage;
        }

        function getCondition(){
            var cnt = 0;
            var condArray = new Array();

            condArray[cnt++] = MeterType.EM;
            condArray[cnt++] = supplierId;
            condArray[cnt++] = $('#locationId').val();
            condArray[cnt++] = $('#tariffIndexId').val();
            condArray[cnt++] = $('#searchDateType').val();
            condArray[cnt++] = $('#searchStartDate').val();
            condArray[cnt++] = $('#searchEndDate').val();
            condArray[cnt++] = $('#seasonalSeasonCombo').val() - 1;

            return condArray;
        }
        
        
        function getCondition2(){
            var cnt = 0;
            var condArray = new Array();

            condArray[0] = MeterType.EM;
            condArray[1] = supplierId;
            condArray[2] = $('#locationId').val();
            condArray[3] = $('#tariffIndexId').val();
            condArray[4] = $('#searchDateType').val();
            
            condArray[7] = $('#seasonalSeasonCombo').val() - 1;
            
            
            
            
            var searchStartDate = $('#searchStartDate').val();
            var searchEndDate = $('#searchEndDate').val();
            
            
            
            //처음 로드시에 날짜가 널일 경우 오늘 날짜로 설정. 
      	    if(searchStartDate =="" && searchEndDate=="")
            {
            	
            	searchStartDate= getToday();
                searchEndDate = getToday();
                searchStartHour="00";
                searchEndHour="23";
            }
       		
       		
       		condArray[5] = searchStartDate;
            condArray[6] = searchEndDate;
            
          /*   alert(condArray[5]);
            alert(condArray[6]); */
            
            

            return condArray;
        }

        function clearSearchItem(){
            $('#locationId').option(0);
            $('#tariffIndexId').option(0);
        }

        function updateFChart() {
	    	emergePre();
	   	    $.getJSON('${ctx}/gadget/mvm/getDemandManagement.do'
	   	    	    ,{meterType:MeterType.EM,
	   	    	    	supplierId:supplierId,
	   	    	    	locationId:$('#locationId').val(),
	   	    	    	tariffType:$('#tariffIndexId').val(),
	   	    	    	dateType:$('#searchDateType').val(),
	   	    	    	startDate:$('#searchStartDate').val(),
	   	    	    	endDate:$('#searchEndDate').val(),
	   	    	    	season:($('#seasonalSeasonCombo').val() - 1)}
					,function(json) {
                         var list = json.result.chart;
                         fcChartDataXml = "<chart "
                       	 	+ "chartLeftMargin='10' "
							+ "chartRightMargin='10' "
							+ "chartTopMargin='10' "
							+ "chartBottomMargin='0' "
                            + "showValues='0' "
                            + "showLabels='1' "
                            + "showLegend='1' "
                            + "labelDisplay='AUTO' "
                            + "legendPosition='RIGHT' "
                            + "numberSuffix=' kW  ' "
                            + "sNumberSuffix=' %' "
                            + "SYAxisMaxValue='100' "
                            + fChartStyle_Common
                            + fChartStyle_Font
                            + fChartStyle_MSColumn3D_nobg
                            + ">";
                    	 var categories = "<categories>";
                         
                    	 var dataset1 = "<dataset seriesName='"+fmtMessage1+"'>";
                    	 var dataset2 = "<dataset seriesName='"+fmtMessage2+"'>";
                    	 var dataset3 = "<dataset seriesName='"+fmtMessage3+"'>";
                    	 var dataset4 = "<dataset seriesName='Load Factor' parentYAxis='S'>";

                        if(list == null || list.length == 0) {
                          categories += "<category label=' ' />";
                          dataset1 += "<set value='' />";
                          dataset2 += "<set value='' />";
                          dataset3 += "<set value='' />";
                          dataset4 += "<set value='' />";
                        } else {
                          $(list).each(function(index) {
                            if(index != "indexOf" && list[index]) {
                              categories += "<category label='"+list[index].location+"' />";
                              dataset1 += "<set value='"+list[index].offPeak+"' />";
                              dataset2 += "<set value='"+list[index].peak+"' />";
                              dataset3 += "<set value='"+list[index].criticalPeak+"' />";
                              dataset4 += "<set value='"+list[index].loadFactor+"' />";
                            }
                          }); 
                         }
                         
                         categories += "</categories>";
                         dataset1 += "</dataset>";
                         dataset2 += "</dataset>";
                         dataset3 += "</dataset>";
                         dataset4 += "</dataset>";
                         
                         fcChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";
                         fcChartRender();

                         hide();
	                }
	   	    );

	   		
		}

	    window.onresize = fcChartRender;
	    function fcChartRender() {
	    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3DLineDY.swf", "myChartId", $('#fcChartDiv').width(), "230", "0", "0");
	        fcChart.setDataXML(fcChartDataXml);
	        fcChart.setTransparent("transparent");
	        fcChart.render("fcChartDiv");
	    }
	    
	    
	    //오늘 날짜를 구한다.,
        function getToday()
        {
        	
        	var currentTime = new Date();
        	var month = (currentTime.getMonth() + 1);
        	var day = currentTime.getDate();//-8
        	var year = currentTime.getFullYear();
        	
        	//alert( day.toString().length );
        	if ( day.toString().length == 1)
			{
        		day = "0" + day.toString();
			}
        	
        	if ( month.toString().length == 1)
			{
        		month = "0" + month.toString();
			}
     
        	
        	
        	var today = year.toString() + month.toString() + day.toString();
        	
        	//alert(today);
        	
        	return today;
        	
        }
        
        
	    
    /*]]>*/
    </script>
     <!-- extJs grid chart style override-->
	<style type="text/css">
    

	/* html {
	overflow: -moz-scrollbars-vertical; 
	overflow-y: scroll;
	}
	 */

	.x-grid3-hd-inner
	{
		text-align: center;
		font-weight: bold;
	}

	 .x-grid3-row-table
	{
	 text-align: center;
	 
	}
	
	.temp
	{
	 text-align: center;
	}
	

	.x-grid3-col-3 
	{
	 text-align: right;
	}
	.x-grid3-col-4 
	{
	 text-align: right;
	}
	.x-grid3-col-5 
	{
	 text-align: right;
	}
	.x-grid3-col-6 
	{
	 text-align: right;
	}
	.x-grid3-col-7 
	{
	 text-align: right;
	}
	
	
	/* .x-grid3-col-6
	{
	 text-align: right;
	}
	 */
	/* .x-grid3-col-7
	{
	 text-align: right;
	 
	}
	
	.x-grid3-col-9
	{
		 text-align: right;
	}
	  */
	
	</style>
</head>
<body>



<!-- search-background DIV (S) -->
<div class="search-bg-withtabs">

	<div class="dayoptions">
	<%@ include file="/gadget/commonDateTab21.jsp"%>
	</div>

	<div class="dashedline"><ul><li></li></ul></div>

	<div class="searchoption-container">
		<table class="searchoption wfree">
			<tr>
				<td><input name="searchWord" id='searchWord' class="billing-searchword" type="text"  value='<fmt:message key="aimir.board.location"/>'/>
					<input type='hidden' id='locationId' value=''></input>
				</td>
				<td><select id="tariffIndexId" style="width:230px;"></select></td>
				<td>
				   <div id="btn">
					   <ul><li><a href="javascript:;" id="btnSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
				   </div>
				</td>
			</tr>
		</table>
	</div>

	<div id="treeeDivOuter" class="tree-billing auto" style="display:none;">
		<div id="treeeDiv"></div>
	</div>
</div>
<!-- search-background DIV (E) -->


<div class="height20px"></div>
<div id="gadget_body">
	<div id="fcChartDiv" style="padding-bottom:5px">
		The chart will appear within this DIV. This text will be replaced by the chart.
	</div>
	<div id="demandMgmtMaxChartGridDiv">
	
	</div>	
	
	<!-- 플랙스 그리드 차트 차후 파일 삭제 demandMgmtMaxChart.swf & mxml-->
	<%-- <object id="demandMgmtMaxChartEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="330px">
		<param name="movie" value="${ctx}/flexapp/swf/demandMgmtMaxChart.swf" />
		<param name="wmode" value="opaque" />
		<!--[if !IE]>-->
			<object id="demandMgmtMaxChartOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/demandMgmtMaxChart.swf" width="100%" height="560px">
			<param name="wmode" value="opaque" />
			
		<!--<![endif]-->
		<div>
			<h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
			<p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
		</div>
		<!--[if !IE]>-->
			</object>
		<!--<![endif]-->
	</object> --%>
	
	
	
</div>



</body>
</html>
