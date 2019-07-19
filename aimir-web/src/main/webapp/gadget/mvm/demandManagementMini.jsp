<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@ include file="/gadget/system/preLoading.jsp"%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><fmt:message key="aimir.demand.management"/></title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/

	    //탭초기화
        var tabs = {hourly:0,period:0,monthlyPeriod:0,yearly:0};
        var tabNames = {};

//        var flex;
        var supplierId = "";
        var serviceType = ServiceType.Electricity;

        var fcChart;
        var fcChartDataXml;

        var fmtMessage1 = "<fmt:message key='aimir.offpeak'/>";
        var fmtMessage2 = "<fmt:message key='aimir.peak'/>";
        var fmtMessage3 = "<fmt:message key='aimir.criticalpeak'/>";
        
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

        $(document).ready(function(){
        	$.ajaxSetup({
                async: false
            });

//            flex = getFlexObject('demandMgmtMiniChart');

            if(tabClickExec){$('#dailySearch').parent().css('display','none');};
            if(tabClickExec){$('#weeklySearch').parent().css('display','none');};
            if(tabClickExec){$('#monthlySearch').parent().css('display','none');};
            if(tabClickExec){$('#weekDailySearch').parent().css('display','none');};
            if(tabClickExec){$('#seasonalSearch').parent().css('display','none');};

            //조회버튼클릭 이벤트 생성
            $(function() { $('#btnSearch').bind('click',function(event) { sendRequest($('#searchDateType').val()); } ); });

            locationTreeGoGo('treeeDiv', 'searchWord', 'locationId');
            getTariffTypeList();
            updateFChart();

            $.ajaxSetup({
                async: true
			});
        });

        

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

        function send(){
//            flex.requestSend();
			updateFChart();
        };

        function getFmtMessage(){
            var cnt = 0;
            var fmtMessage = new Array();

            fmtMessage[cnt++] = "<fmt:message key='aimir.number'/>";                // 번호
            fmtMessage[cnt++] = "<fmt:message key='aimir.location.supplier'/>";     // 공급지역
            fmtMessage[cnt++] = "<fmt:message key='aimir.contract.tariff.type'/>";  // 계약종별
            fmtMessage[9] = "<fmt:message key='aimir.alert'/>";

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

        function clearSearchItem(){
            $('#searchWord').val("");
            $('#locationId').val("");
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
                         	 + "chartLeftMargin='0' "
						 	 + "chartRightMargin='0' "
						 	 + "chartTopMargin='10' "
						 	 + "chartBottomMargin='0' "
                             + "showValues='0' "
                             + "showLabels='1' "
                             + "showLegend='1' "
                             + "labelDisplay = 'AUTO' "
                             + "numberSuffix=' kW ' "
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
                    	 var dataset4 = "<dataset seriesName='' parentYAxis='S'>";

                         if(list == null || list.length == 0) {
                        	 categories += "<category label=' ' />";
                        	 dataset1 += "<set value='' />";
                        	 dataset2 += "<set value='' />";
                        	 dataset3 += "<set value='' />";
                        	 dataset4 += "<set value='' />";
                         } else {
                        	 for( index in list){
                            	 if(index != "indexOf") {
	                               	 categories += "<category label='"+list[index].location+"' />";
	                               	 dataset1 += "<set value='"+list[index].offPeak+"' />";
	                               	 dataset2 += "<set value='"+list[index].peak+"' />";
	                               	 dataset3 += "<set value='"+list[index].criticalPeak+"' />";
	                               	 dataset4 += "<set value='"+list[index].loadFactor+"' />";
                            	 }
                             }
                         }
                         
                         categories += "</categories>";
                         dataset1 += "</dataset>";
                         dataset2 += "</dataset>";
                         dataset3 += "</dataset>";
                         dataset4 += "</dataset>";
                         
                         fcChartDataXml += categories + dataset1 + dataset2 + dataset3 + dataset4 + "</chart>";
                         fcChartRender();

                         hide();
	                });

	   		
		}

	    window.onresize = fcChartRender;
	    function fcChartRender() {
	    	if($('#fcChartDiv').is(':visible')) {
		    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3DLineDY.swf", "myChartId", $('#fcChartDiv').width(), "210", "0", "0");
		        fcChart.setDataXML(fcChartDataXml);
		        fcChart.setTransparent("transparent");
		        fcChart.render("fcChartDiv");
	    	}
	    }
    /*]]>*/
    </script>
</head>
<body>
	<!-- search-background DIV (S) -->
	<input type='hidden' id='locationId' value=''></input>
	<div class="search-bg-withtabs">
		<div class="dayoptions">
			<%@ include file="/gadget/commonDateTab.jsp"%>
		</div>
		<div class="dashedline"></div>
		<div class="searchoption-container">
			<table class="searchoption wfree">
				<tr>
				<td><input name="searchWord" id='searchWord' style="width:120px" type="text" value='<fmt:message key="aimir.location"/>'/></td>				
				<td><select id="tariffIndexId" style="width:200px;"></select></td>
				<td><a href="javascript:;" id="btnSearch" class="btn_blue"><span><fmt:message key="aimir.button.search" /></span></a></td>
				</tr>
			</table>
		</div>
		<div id="treeeDivOuter" class="tree-billing auto" style="display:none;">
			<div id="treeeDiv"></div>
		</div>
	</div>
	<!-- search-background DIV (E) -->
	
	<div class="gadget_body2">
		<div id="fcChartDiv" style="padding-top:5px">
			The chart will appear within this DIV. This text will be replaced by the chart.
		</div>
	</div>
</body>
</html>
