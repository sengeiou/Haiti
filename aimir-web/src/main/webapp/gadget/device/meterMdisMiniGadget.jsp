<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/gadget/system/preLoading.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Meter MiniGadget</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>

<script type="text/javascript" charset="utf-8">

    var flex;
    var miniTab = "ml";
    var supplierId = "";

    var fcChartDataXml;
    var fcChart;

    $.ajaxSetup({
        async: false
    });

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

    $(function(){

    	flex = getFlexObject('meterMiniChart');


        $(function() { $('#_meterType') .bind('click',function(event) { changeData($('#sMeterType').val());  } ); });
        $(function() { $('#_loc')       .bind('click',function(event) { changeData($('#sLoc').val());        } ); });
        $(function() { $('#_commStatus').bind('click',function(event) { changeData($('#sCommStatus').val()); } ); });

        $("#meterMini").tabs();

        // Select
        $("#sMeterType").selectbox();
        $("#sCommStatus").selectbox();
        $("#sLoc").selectbox();

    });


    function getFmtMessage(){
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.type2"/>";

        return fmtMessage;
    }

    function getFmtMessageCommAlert(){
        var fmtMessage = new Array();;

        fmtMessage[0] = "<fmt:message key="aimir.normal"/>";
        fmtMessage[1] = "<fmt:message key="aimir.commstateYellow"/>";
        fmtMessage[2] = "<fmt:message key="aimir.commstateRed"/>";

        return fmtMessage;
    }



    function getCondition(){
        var condArray = new Array();

        condArray[0] = miniTab;
        condArray[1] = supplierId;

        return condArray;
    }

   // Tab / Combo 의 변경으로 조회대상 데이터 변경
    function changeData(selMiniTab){
    	miniTab = selMiniTab;
    	
    	updateFChart();
    	flex.requestSend();
    	
    }

    $(document).ready(function() {		    
    	updateFChart();
    });
    
    function updateFChart() {
    	emergePre();
    	$.getJSON('${ctx}/gadget/device/getMeterMiniChartMdis.do'
   	    	    ,{meterChart:miniTab, 
   	    	    	supplierId:supplierId}
				,function(json) {
					 var chartSeries = json.chartSeries;
                     var chartData = json.chartData;
                     fcChartDataXml = "<chart "
                    	+ "chartLeftMargin='0' "
						+ "chartRightMargin='0' "
						+ "chartTopMargin='5' "
						+ "chartBottomMargin='0' "
  	                    + "showValues='0' "
 	                    + "showLabels='1' "
 	                    + "showLegend='1' "
 	                    + "yaxismaxvalue='5'"
 	                    + "labelDisplay = 'WRAP' "
 	                   	+ "numberSuffix='  ' "
 	                    + fChartStyle_legendScroll
 	                    + fChartStyle_Common
 	                    + fChartStyle_Font
 	                    + fChartStyle_MSColumn3D_nobg
 	                    + ">";
                     var categories = "<categories>";
                     var datasets = new Array(chartSeries.length);

                     var size = 0;

                     for( index in chartSeries) {
                    	 if(chartSeries[index].displayName != "") {
	                    	 if(chartSeries[index].displayName == "fmtMessage00") datasets[index] = "<dataset seriesName='<fmt:message key='aimir.normal'/>'>";
	                         else if(chartSeries[index].displayName == "fmtMessage24") datasets[index] = "<dataset seriesName='<fmt:message key='aimir.commstateYellow'/>'>";
	                         else if(chartSeries[index].displayName == "fmtMessage48") datasets[index] = "<dataset seriesName='<fmt:message key='aimir.commstateRed'/>'>";
	                         else datasets[index] = "<dataset seriesName='"+chartSeries[index].displayName+"'>";

	                         size++;
                    	 }
                     }

                     for(index in chartData) {                         
                    	 if(chartData[index].xTag == "fmtMessage00") categories += "<category label='<fmt:message key='aimir.normal'/>' />";
                         else if(chartData[index].xTag == "fmtMessage24") categories += "<category label='<fmt:message key='aimir.commstateYellow'/>' />";
                         else if(chartData[index].xTag == "fmtMessage48") categories += "<category label='<fmt:message key='aimir.commstateRed'/>' />";
                         else categories += "<category label='"+chartData[index].xTag+"' />";

                    	 for(var i=0; i < size ; i++) {
                        	 if(eval("chartData[index].value" + i) > 0)
                    		 	 datasets[i] += "<set value='"+eval("chartData[index].value" + i)+"' />";
                    		 else
                    			 datasets[i] += "<set value='' />";
                    	 }
                     }

                     categories += "</categories>";

                     for( index in chartSeries) {
                    	 if(chartSeries[index].displayName != "") {
                    	 	datasets[index] += "</dataset>";
                    	 }
                     }
  	               
  	                 fcChartDataXml += categories;

	  	             for( index in chartSeries) {
	  	            	if(chartSeries[index].displayName != "") {
	                  	 	fcChartDataXml += datasets[index];
	  	            	}
	                 }
	  	             fcChartDataXml += "</chart>";                     
                     fcChartRender();

                     hide();
                }
   	    );
	}

    window.onresize = fcChartRender;
    function fcChartRender() {
    	if($('#fcChartDiv').is(':visible')) {
	    	fcChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "fcChartId", $('#fcChartDiv').width(), "160", "0", "0");
	    	fcChart.setDataXML(fcChartDataXml);
	    	fcChart.setTransparent("transparent");
	    	fcChart.render("fcChartDiv");
    	}

    }

    </script>

</head>
<body>

    <div id="meterMini">
        <ul>
            <li><a href="#meterType"  id="_meterType" ><fmt:message key="aimir.metertypily"/></a></li>
            <li><a href="#loc"        id="_loc"><fmt:message key="aimir.locally"/></a></li>
            <li><a href="#commStatus" id="_commStatus"><fmt:message key="aimir.commstatusly"/></a></li>
        </ul>


		<!--  미터유형별 Tab  -->
        <div id="meterType" class="meterMiniLayer">
			<select id="sMeterType" name="" style="width:130px" onChange="javascript:changeData(this.value);">
				<option value="ml"><fmt:message key="aimir.locally"/></option>
				<option value="mc"><fmt:message key="aimir.commstatusly"/></option>
			</select>
        </div>

        <!--  지역별 Tab  -->
        <div id="loc" class="meterMiniLayer">
			<select id="sLoc" name="" style="width:130px" onChange="javascript:changeData(this.value);">
				<option value="lm"><fmt:message key="aimir.metertypily"/></option>
				<option value="lc"><fmt:message key="aimir.commstatusly"/></option>
			</select>
        </div>

        <!-- 통신상태별 Tab -->
        <div id="commStatus" class="meterMiniLayer">
			<select id="sCommStatus" name="" style="width:130px" onChange="javascript:changeData(this.value);">
				<option value="cm"><fmt:message key="aimir.metertypily"/></option>
				<option value="cl"><fmt:message key="aimir.locally"/></option>
			</select>
        </div>

    </div>


	<div class="div-meter gadget_body2">
		<div id="fcChartDiv" class="margin-t5px" style="height:170px; vertical-align:middle;">
		    The chart will appear within this DIV. This text will be replaced by the chart.
		</div>
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="85px" id="meterMiniChartEx">
				<param name="movie" value="${ctx}/flexapp/swf/meterMiniChartMdis.swf" />
				<param name="wmode" value="opaque">

				<!--[if !IE]>-->
				<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/meterMiniChartMdis.swf" width="100%" height="85px" id="meterMiniChartOt">
				<param name="wmode" value="opaque">

				<!--<![endif]-->
				<p>Alternative content</p>
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
		</object>
    </div>



</body>
</html>