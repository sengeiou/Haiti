<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<%@ page import="com.aimir.constants.CommonConstants"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Customer Usage MiniGadget</title>

<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" />
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/extjs/ext-all.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/FusionCharts.js"></script>

<script type="text/javascript" charset="utf-8">

    var condArray   = new Array();  //파라미터
    var MONTHLY     = <%=CommonConstants.DateType.valueOf("MONTHLY").getCode()%>;
    var userId      = "";
    var currencySymbol = "";
    var iStand      = 0;
    var condArray   = new Array();

    var thisMonthTotalFee = 0.0;
    var preMonthTotalFee = 0.0;

    var EMguageDivWidth;
    var GMguageDivWidth;
    var WMguageDivWidth;

	/**
	 * 유저 세션 정보 가져오기
	 */
	$.getJSON('${ctx}/common/getUserInfo.do',
	        function(json) {
	            if(json.currencySymbol != ""){
	            	currencySymbol = json.currencySymbol;
	            }
	        }
	);

	$(function(){
		googleCall("seoul");
		getData();

		 EMguageDivWidth  = $('#EMguageChartDiv').width();
		 GMguageDivWidth  = $('#GMguageChartDiv').width();
		 WMguageDivWidth  = $('#WMguageChartDiv').width();

	});
	

	//구글 날씨
	function googleCall(area){
	    $.post(
	            // "../../googleCall.jsp",
	            "${ctx}/gadget/customer/getGoogleWeather.do",
	             {"area":area},
	             googleCall_callback
	    ); //end $.post
	}

	//콜백
	function googleCall_callback(json, textStatus){
	    if(json.ERROR != null){
	        Ext.Msg.alert('<fmt:message key='aimir.message'/>',json.ERROR);
	        return;
	    }

	    var result = "";

	    jQuery.each(json.xmlData, function(key, obj) {
	        jQuery.each(obj, function(key2, obj2){
	            if(key2 == "current_conditions"){
	            	//result += "[현재날씨]" + obj2.condition +  " :" + "\n" + "<img src='http://www.google.co.kr" + obj2.icon + "' alt= '" + obj2.condition + "'/><br>"
	            	//+ "[온도]" + obj2.temp_c;

	            	$('#icon').html("<img width='100%' height='100%' src='http://www.google.co.kr" + obj2.icon + "' alt= '" + obj2.condition + "'/>");
	            	$('#weather').html(obj2.condition);

	            	$('#temp').html(obj2.temp_c + " &#176; ");
	            }

	        });
	    });

	   // $('#divGoogle').html(result);
	}

	//데이터 가져오기
    function getData(){


        getCondition();

        $.post(
                "${ctx}/gadget/customer/getCustomerMainGadgetUsageFee.do",
                 {"sViewType":condArray[0], "sUserId":condArray[1], "iMdev_type":condArray[2], "iStand":condArray[3]},
                 setData_callback
        ); //end $.post
    }

  //조회조건 가져오기
    function getCondition(){
        condArray[0]    = MONTHLY;
        condArray[1]    = userId;
        condArray[2]    = 'Meter';       // DeviceType => MDEV_TYPE
        condArray[3]    = iStand;

        //alert("jsp userId : " + userId);

        return condArray;
    }

    //콜백
    function setData_callback(json, textStatus){

         var i = 0;
         var j = 0;

         var emCo2Val = 0.0;
         var gmCo2Val = 0.0;
         var wmCo2Val = 0.0;

         var thisMonth = parseInt(json.sEndData.sEnd);

         //alert("json.sEndData.sEnd : " + json.sEndData.sEnd);

         if(json.emCo2 != ""){
        	 emCo2Val = parseFloat(json.emCo2.co2);
         }

         if(json.gmCo2 != ""){
             gmCo2Val = parseFloat(json.gmCo2.co2);
         }

         if(json.wmCo2 != ""){
             wmCo2Val = parseFloat(json.wmCo2.co2);
         }

         $('#co2').html(emCo2Val + gmCo2Val + wmCo2Val);

         //alert(json.emCo2.co2 + " : " + json.gmCo2.co2 + " : " + json.wmCo2.co2);
         //alert(json.emCo2 + " : " + json.gmCo2 + " : " + json.wmCo2);

         jQuery.each(json.usageEMData, function(key, obj) {
             jQuery.each(obj, function(key2, obj2){

                 //alert(key2 + " : " + obj2 + " :: " + i);

                 if(key2 == "usage"){

                	 if(i == 0)
                     {
                         $('#emUsage').html(obj2);

                      // 게이지차트 조립
                         var EMguageChartDataXml = " <chart lowerLimit='0' upperLimit='500' showGaugeBorder='0' " +
                         " gaugeOuterRadius='55%' gaugeInnerRadius='45%' pivotRadius='2' " +
                         " lowerLimitDisplay='0kWh' "  +
                         " gaugeStartAngle='180' gaugeEndAngle='0' " +
                         " palette='5' numberSuffix='kWh' tickValueDistance='18' showValue='1' paletteThemeColor='26b6bf' " +
                         " pivotFillColor='333333' pivotFillAlpha='100' pivotFillMix='' " +
                         " showPivotBorder='1' pivotBorderThickness='3' pivotBorderColor='CCCCCC' pivotBorderAlpha='0' > "
                      + " <colorRange>"
                         + " <color minValue='0' maxValue='166' code='45E2BD'/> "
                         + " <color minValue='166' maxValue='332' code='FFDD37'/> "
                         + " <color minValue='300' maxValue='500' code='AB66DB'/> "
                      + " </colorRange> "
                      + " <dials>"
                         + " <dial value='"+obj2+"' rearExtension='10'/> "
                      + " </dials>"
                      + " </chart>";
	                      if($('#EMguageChartDiv').is(':visible')) {
	                          var EMguageChart = new FusionCharts("${ctx}/flexapp/swf/FWidgets/AngularGauge.swf", "myChartIdEM", EMguageDivWidth, "85", "0", "0");
	                          EMguageChart.setDataXML(EMguageChartDataXml);
	                          EMguageChart.setTransparent("transparent");
	                          EMguageChart.render("EMguageChartDiv");
	                      }
                     }else if(1 == 1){
                     }

                     i++;
                 }

                 if(key2 == "price"){

                     if(j==0)
                     {
                         $('#emFee').html(obj2 + " " + currencySymbol);
                         thisMonthTotalFee += parseFloat(obj2);

                     }else if(j==1){
                         preMonthTotalFee += parseFloat(obj2);
                     }

                     j++;
                 }

             });
         });

         i=0;
         j=0;

         jQuery.each(json.usageGMData, function(key, obj) {
             jQuery.each(obj, function(key2, obj2){

                 //alert(key2 + " : " + obj2);

                 if(key2 == "usage"){

                     if(i == 0)
                     {
                         $('#gmUsage').html(obj2);

                      // 게이지차트 조립
                         var GMguageChartDataXml = " <chart lowerLimit='0' upperLimit='500' showGaugeBorder='0' " +
                         " gaugeOuterRadius='55%' gaugeInnerRadius='45%' pivotRadius='2' " +
                         " lowerLimitDisplay='0m3' "  +
                         " gaugeStartAngle='180' gaugeEndAngle='0' " +
                         " palette='5' numberSuffix='m3' tickValueDistance='18' showValue='1' paletteThemeColor='87b145' " +
                         " pivotFillColor='333333' pivotFillAlpha='100' pivotFillMix='' " +
                         " showPivotBorder='1' pivotBorderThickness='3' pivotBorderColor='CCCCCC' pivotBorderAlpha='0' > "
                      + " <colorRange>"
                         + " <color minValue='0' maxValue='166' code='45E2BD'/> "
                         + " <color minValue='166' maxValue='332' code='FFDD37'/> "
                         + " <color minValue='300' maxValue='500' code='AB66DB'/> "
                      + " </colorRange> "
                      + " <dials>"
                         + " <dial value='"+obj2+"' rearExtension='10'/> "
                      + " </dials>"
                      + " </chart>";
	                      if($('#GMguageChartDiv').is(':visible')) {
	                          var GMguageChart = new FusionCharts("${ctx}/flexapp/swf/FWidgets/AngularGauge.swf", "myChartIdGM", GMguageDivWidth, "85", "0", "0");
	                          GMguageChart.setDataXML(GMguageChartDataXml);
	                          GMguageChart.setTransparent("transparent");
	                          GMguageChart.render("GMguageChartDiv");
	                      }
                     }else if(1 == 1){
                     }

                     i++;
                 }

                 if(key2 == "price"){

                     if(j==0)
                     {
                         $('#gmFee').html(obj2 + " " + currencySymbol);
                         thisMonthTotalFee += parseFloat(obj2);
                     }else if(j==1){
                         preMonthTotalFee += parseFloat(obj2);
                     }

                     j++;
                 }

             });
         });


         i=0;
         j=0;

         jQuery.each(json.usageWMData, function(key, obj) {
             jQuery.each(obj, function(key2, obj2){

                 //alert(key2 + " : " + obj2);

                 if(key2 == "usage"){

                     if(i == 0)
                     {
                         $('#wmUsage').html(obj2);

                      // 게이지차트 조립
                         var WMguageChartDataXml = " <chart lowerLimit='0' upperLimit='100' showGaugeBorder='0' " +
                         " gaugeOuterRadius='55%' gaugeInnerRadius='45%' pivotRadius='2' " +
                         " lowerLimitDisplay='0m3' "  +
                         " gaugeStartAngle='180' gaugeEndAngle='0' " +
                         " palette='5' numberSuffix='m3' tickValueDistance='18' showValue='1' paletteThemeColor='5688e7' " +
                         " pivotFillColor='333333' pivotFillAlpha='100' pivotFillMix='' " +
                         " showPivotBorder='1' pivotBorderThickness='3' pivotBorderColor='CCCCCC' pivotBorderAlpha='0' > "
                      + " <colorRange>"
                         + " <color minValue='0' maxValue='33' code='45E2BD'/> "
                         + " <color minValue='33' maxValue='66' code='FFDD37'/> "
                         + " <color minValue='66' maxValue='100' code='AB66DB'/> "
                      + " </colorRange> "
                      + " <dials>"
                         + " <dial value='"+obj2+"' rearExtension='10'/> "
                      + " </dials>"
                      + " </chart>";
	                      if($('#WMguageChartDiv').is(':visible')) {
	                          var WMguageChart = new FusionCharts("${ctx}/flexapp/swf/FWidgets/AngularGauge.swf", "myChartIdWM", WMguageDivWidth, "85", "0", "0");
	                          WMguageChart.setDataXML(WMguageChartDataXml);
	                          WMguageChart.setTransparent("transparent");
	                          WMguageChart.render("WMguageChartDiv");
	                      }
                     }else if(1 == 1){
                     }

                     i++;
                 }

                 if(key2 == "price"){

                     if(j==0)
                     {
                         $('#wmFee').html(obj2 + " " + currencySymbol);
                         thisMonthTotalFee += parseFloat(obj2);
                     }else if(j==1){
                         preMonthTotalFee += parseFloat(obj2);
                     }

                     j++;
                 }

             });
         });


         $('#thisMonthUsageFee').html(thisMonthTotalFee+" "+currencySymbol);
         $('#preMonthUsageFee').html(preMonthTotalFee+" "+currencySymbol);
    }

</script>

</head>
<body class="customer-main-bg">


	<table class="customer-main-weather wfree">
		<tr>
			<td><img src="${ctx}/flexapp/swf/assets/energy_ic_co2_2.png"/></td>
			<td><span class="font-weather" id="co2"></span></td>
			<td><span class="font-unit">g&#47;h</span></td>
			<td class="weather-space"></td>
			<td><div id="icon" class="icon-googleweather"></div></td>
			<td><!-- <div class='gray11pt'><fmt:message key="aimir.currentTemperature"/></div> --><div id="temp" class="font-weather"></div></td>
		</tr>
	</table>


    <!-- 데이터 출력 (S) -->
    <div id="divGoogle"></div>

    <form id="dataForm1">
    <div id="ymdData" class="gadget_body">

        <table class="border-2px-sum">
            <tr class="customer-main-usage">
                <td>
					<table class="usagetariff-sum">
						<tr><td class="gray11pt"><fmt:message key="aimir.thisMonthFee"/><!-- 이번달 사용요금 --></td></tr>
						<tr><td class="font-sum bold" id="thisMonthUsageFee">-</td></tr>
					</table>
				</td>
                <td>
					<table class="usagetariff-sum">
						<tr><td class="gray11pt"><fmt:message key="aimir.preMonthFee"/><!-- 지난달 사용요금 --></td></tr>
						<tr><td class="font-sum bold" id="preMonthUsageFee">-</td></tr>
					</table>
				</td>
            </tr>
        </table>
    </div>
    </form>



    <form id="dataForm2">
    <div id="ymdData" class="gadget_body2">

        <table class="customer-main-guage border-2px-elec">
            <tr>
				<td class="title elec11pt"><fmt:message key="aimir.energymeter"/></td>
                <td class="chart"><div id="EMguageChartDiv"></div></td>
                <td class="amount">
                    <div class="dottedline"><span class="values-a elec10pt">&nbsp;kWh</span><span id="emUsage" class="values-a elec10pt"></span></div>
					<div class="dottedline"><span id="emFee" class="values-b elec14pt bold"></span></div>
                </td>
            </tr>
		</table>

        <table class="customer-main-guage border-2px-gas">
            <tr>
				<td class="title gas11pt"><fmt:message key="aimir.gas"/></td>
                <td class="chart"><div id="GMguageChartDiv"></div></td>
                <td class="amount">
                    <div class="dottedline"><span class="values-a gas10pt">&nbsp;m3</span><span id="gmUsage" class="values-a gas10pt"></span></div>
					<div class="dottedline"><span id="gmFee" class="values-b gas14pt bold"></span></div>
                </td>
            </tr>
        </table>

        <table class="customer-main-guage border-2px-water">
            <tr>
				<td class="title water11pt"><fmt:message key="aimir.water"/></td>
                <td class="chart"><div id="WMguageChartDiv"></div></td>
                <td class="amount">
                    <div class="dottedline"><span class="values-a water10pt">&nbsp;m3</span><span id="wmUsage" class="values-a water10pt"></span></div>
					<div class="dottedline"><span id="wmFee" class="values-b water14pt bold"></span></div>
                </td>
            </tr>
		</table>


	</div>
    </form>
    <!-- 데이터 출력 (E) -->




</body>
</html>