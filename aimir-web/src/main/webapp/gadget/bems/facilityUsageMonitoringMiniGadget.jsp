<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title></title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">

<!-- %@ include file="/gadget/system/preLoading.jsp"%-->
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //플렉스객체 
    var flex;
    var periodGlobalData;
    var classifyGlobalData;
 	var supplierId;
    // FusionChart가 정상적으로 동작하지 않을시 debugging용	
// 	FusionCharts.addEventListener('Error', function (eventObject, argumentsObject) {
// 		   alert('There was an error with  charts!\n' + argumentsObject.message);
// 	});
 
$(document).ready(function(){		
    	var sId;
    	$.getJSON('${ctx}/common/getUserInfo.do',
    			function(json) {
    				if(json.supplierId != ""){
    					sId = json.supplierId;
    					if(sId != "") {
    						supplierId= sId;
    						
    						$("#dailyStartDate")    .datepicker({
    				    		maxDate:'+0m',showOn: 'button', 
    				    		dateFormat:'yymmdd',
    				    		buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', 
    				        	onSelect: function(dateText) {  modifyDate(dateText);},
    				        	buttonImageOnly: true});

    						 $("#dailyStartDate").val($.datepicker.formatDate('yymmdd', new Date()));
    						 modifyDate($("#dailyStartDate").val());
    					}
    				}
    		}); 

    	 $(function() { $('#dailyLeft')      .bind('click',  function(event) { dailyArrow(-1); } ); });
    	 $(function() { $('#dailyRight')     .bind('click',  function(event) { dailyArrow(1); } ); });
    	 $('#periodType').selectbox();    		
     	 $("#usageTab").tabs();
    	 
          // 브라우저별로 플렉스객체를 초기화한다.        
       	 flex = getFlexObject('facilityUsageMonitoringMini');
        
    });


    // datepicker로 선택한 날짜의 포맷 변경
    function modifyDate(setDate){
    	
        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                    $("#viewDate").val(json.localDate);
                });
    }
    
    /**
     * 일별 화살표처리
     */
     function dailyArrow(val){
    	 var date = $("#dailyStartDate").datepicker("getDate");
    	 var current = new Date();
         
    	 if(date.toDateString() === current.toDateString() && val > 0 ){
            return;
         }
		 date.setDate(date.getDate() + val);
		 $("#dailyStartDate").datepicker("setDate",date);
		 
		 modifyDate($("#dailyStartDate").val());
     }

    /**
     * 공통 send 거래
     * 개별 화면에서 각각 구현해야한다.
     * 조회버튼클릭시,조회데이터 변경시 최종적으로 호출하게 된다.
     */
    function search(){
        
		if (flex != null) {
            flex.getUsage();
        }
    }


    /**
     * Flex 에서 메세지를 조회하기위한 함수
     */
    function getFmtMessage(){
        var fmtMessage = new Array();

        fmtMessage[0] = '<fmt:message key="aimir.alert"/>';// 장애
        fmtMessage[1] = '<fmt:message key="aimir.totalusage"/>';//"총사용량";
        fmtMessage[2] = '<fmt:message key="aimir.co2formula2"/>';//"Co2배출량";
        fmtMessage[3] = '<fmt:message key="aimir.min"/>';//"최소";
        fmtMessage[4] = '<fmt:message key="aimir.max"/>';//"최대";
        fmtMessage[5] = '<fmt:message key="aimir.classificationusage"/>';//"분류별사용량";
        fmtMessage[6] = '<fmt:message key="aimir.usage"/>'+' [kgOE]';//"사용량 [kgOE]";
        fmtMessage[7] = '<fmt:message key="aimir.electricity"/>';//"전기";
        fmtMessage[8] = '<fmt:message key="aimir.gas"/>';//"가스";
        fmtMessage[9] = '<fmt:message key="aimir.water"/>';//"수도";
        
        fmtMessage[10] = '<fmt:message key="aimir.facilityUsageMonitoring"/>';//"설비별 사용량 모니터링";
        fmtMessage[11] = '<fmt:message key="aimir.searchDate"/>';//"조회기준일자";
        fmtMessage[12] = '<fmt:message key="aimir.facilityClassification"/>';//"설비분류";
        fmtMessage[13] = '<fmt:message key="aimir.all"/>';//"전체";
        fmtMessage[14] = '<fmt:message key="aimir.searchPeriod"/>';//"조회주기";
        fmtMessage[15] = '<fmt:message key="aimir.usageFee2"/>';//"사용요금";
        fmtMessage[16] = '<fmt:message key="aimir.price.unit"/>';//"원";
        fmtMessage[17] = '<fmt:message key="aimir.facilityUsage"/>';//"설비별 사용량";
        fmtMessage[18] = '<fmt:message key="aimir.periodUsage"/>';//"주기별 사용량";
        fmtMessage[19] = '<fmt:message key="aimir.sum"/>';//"합계";
        fmtMessage[20] = '<fmt:message key="aimir.hour2"/>'; //"시";
        fmtMessage[21] = '<fmt:message key="aimir.dayofweek"/>';//요일
        fmtMessage[22] = '<fmt:message key="aimir.month"/>';//월
        fmtMessage[23] = '<fmt:message key="aimir.quarter"/>';//분기
        fmtMessage[24] = '<fmt:message key="aimir.heatmeter"/>'; //열량 
        fmtMessage[25] = '<fmt:message key="aimir.locationUsage.energyUsage"/>';                 //전기 사용량
        fmtMessage[26] = '<fmt:message key="aimir.locationUsage.gasUsage"/>';                 //가스 사용량
        fmtMessage[27] = '<fmt:message key="aimir.locationUsage.waterUsage"/>';                 //수도 사용량
        fmtMessage[28] = '<fmt:message key="aimir.facilityMgmt.heat"/>';                 //열량 사용량   
        return fmtMessage;
    }

    /**
     * Flex 에서 조회조건을 조회하기위한 함수.
     */
    function getParams(){
        var param = new Object();

        param.periodType = $('#periodType').val();
        param.periodTypeName = $('#periodType option:selected').text();
		param.searchDate = $("#dailyStartDate").val();

		if($('#periodType').val() == DateType.DAILY){
        	param.periodValueName = '<fmt:message key="aimir.hour2"/>';//'시';
        }else if($('#periodType').val() == DateType.WEEKLY){
        	param.periodValueName = '<fmt:message key="aimir.dayofweek"/>';//'요일';
        }else if($('#periodType').val() == DateType.MONTHLY){
        	param.periodValueName = '<fmt:message key="aimir.month"/>';//'월';
        }else if($('#periodType').val() == DateType.QUARTERLY){
        	param.periodValueName = '<fmt:message key="aimir.quarter"/>';//'분기';
        }
        
        return param;
    }

    /**
     * Flex 에서 조회조건을 조회하기위한 함수.
     */
    function setDate(){
    	getCurrentTime();
    }

    function getCurrentTime(){

		$.getJSON('${ctx}/common/getUserInfo.do',
	            function(json) {
	                if(json.supplierId != ""){
	                    supplierId = json.supplierId;
	                    currTime = json.currTime;
	                    $('#currDate').html(currTime);
	                }
	            }
	    );
	}

    function doPrint(){
        flex.doPrint();
    }

    window.resize = setGraph;
    function setGraph(periodData,classifyData,array){
    	var periodUsageDiv = $('div#periodUsage');
    	var classificationusageDiv = $('div#classificationusage');
    	var width;
    	var periodType = $('#periodType').val();
    	var labelStep;
    	
    	if ( periodUsageDiv.css('display') !== 'none' ) {
    		width = periodUsageDiv.width(); 
    	} else if ( classificationusageDiv.css('display') !== 'none') {
    		width = classificationusageDiv.width();
    	}
    	
    	if ( periodType == 1 ) {
    		labelStep = 2;
    	} else {
    		labelStep = 1;
    	}
    	
            if(periodData){
            	periodData="<chart chartLeftMargin='0' "
            	+ "labelStep='"+labelStep+"' "
            	+ "labelDisplay='NONE' "
                + "chartRightMargin='0' "
    			+ "chartTopMargin='5' "
    			+ "chartBottomMargin='5' "
    			+ fChartStyle_Common
                + fChartStyle_Font
    			+ fChartStyle_StColumn3D_nobg
    			+ periodData;
    	   
            	periodGlobalData= periodData;
    	    }
            var periodChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3DLineDY.swf", "periodChartId", width,  150, "0", "0");            
            if(periodData)
            	periodChart.setDataXML(periodData);
            else{
            	periodChart.setDataXML(periodGlobalData);
            }
            periodChart.setTransparent("transparent");           
            periodChart.render("periodUsage");


            if(classifyData){
            	classifyData="<chart chartLeftMargin='0' "
            	+ "pieRadius='45' "//fChartStyle_Pie3D_nobg에서  radius만 재정의
            	+ "chartRightMargin='0' "
    			+ "chartTopMargin='5' "
    			+ "chartBottomMargin='0' "
    			+ fChartStyle_Common
                + fChartStyle_Font
    			+ fChartStyle_Pie3D_nobg
    			+ classifyData;
            	classifyGlobalData= classifyData;
    	    }
            var classifyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Doughnut3D.swf", "classifyChartId", width,  140, "0", "0");            
            if(classifyData)
            	classifyChart.setDataXML(classifyData);
            else{
            	classifyChart.setDataXML(classifyGlobalData);
            }
            classifyChart.setTransparent("transparent");           
            classifyChart.render("classificationusage");


            $('#grid0').html(array[0]);
            $('#grid1').html(array[1]);
            $('#grid2').html(array[2]);
            $('#grid3').html(array[3]);
            $('#grid4').html(array[4]);
            $('#grid5').html("<em class='Trd_normal'>"+array[5]+"</em> : "+array[6]);
            $('#grid6').html("<em class='Trd_normal'>"+array[7]+"</em> : "+array[8]);
    }
    
    //======================================================================================

/*]]>*/
    </script>
</head>
<body>
<div id="wrapper">
    
    <div id="container2">
    
       <!-- 주기 및 날짜 (S) -->
      	<div class="tapBg ptb5">
        <ul class="noTapSearch">
            <li class="tit_default"><fmt:message key="aimir.locationUsage.term"/></li>
            <li>
                <select id="periodType" style="width:60px">
                  <option value="1"><fmt:message key="aimir.locationUsage.day"/></option>
                  <option value="3"><fmt:message key="aimir.locationUsage.week"/></option>
                  <option value="4"><fmt:message key="aimir.locationUsage.month"/></option>
                  <option value="9"><fmt:message key="aimir.quarter"/></option>
                </select>
            </li> 
           	<li><button id="dailyLeft" type="button" class="backicon srrow" ></button></li>
		   	<li>
		   		<input id="viewDate" type="text" readonly="readonly" style="width:70px">
		   		<input id="dailyStartDate" type="text" readonly="readonly" style="display:none;">
		   	</li>
		   	<li><button id="dailyRight" type="button" class="nexticon srrow" ></button></li>
			<li><em class="bems_button"><a href="javascript:search()" id="dailySearch"><fmt:message key="aimir.locationUsage.search"/></a></em></li>
<!-- 			<li class="mleft5"><em class="bems_button"><a href="javascript:doPrint()">print</a></em></li>            -->
        </ul>       
       </div>
        <!-- 주기 및 날짜  (E) -->
 
        <!-- 탐색기 및 사용량 테이블 (S) -->
        <div class="Bchart clear">
        	<div class="mtrl10" style="height:130px">
				<div class="float_left width_35">
				<object id="facilityUsageMonitoringMiniEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="120" codebase="http://fpdownload.macromedia.com/get/flashplayr/current/swflash.cab">
				<param name="movie" value="${ctx}/flexapp/swf/bems/facilityUsageMonitoringMini.swf">
				<param name="quality" value="high">
				<param name="wmode" value="opaque">
				<param name="swfversion" value="9.0.45.0">
				<!-- This param tag prompts users with Flash Player 6.0 r65 and higher to download the latest version of Flash Player. Delete it if you don’t want users to see the prompt. -->
				<param name="expressinstall" value="Scripts/expressInstall.swf">
				<!-- Next object tag is for non-IE browsers. So hide it from IE using IECC. -->
				<!--[if !IE]>-->
				<object id="facilityUsageMonitoringMiniOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/facilityUsageMonitoringMini.swf" width="100%" height="120">
				<!--<![endif]-->
				<param name="quality" value="high">
				<param name="wmode" value="opaque">
				<param name="swfversion" value="9.0.45.0">
				<param name="expressinstall" value="Scripts/expressInstall.swf">
				<param name="allowScriptAccess" value="always">
				<!-- The browser displays the following alternative content for users with Flash Player 6.0 and older. -->
				<div>
				  <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
				  <p><a href="http://www.adobe.com/go/getflashplayr"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
				</div>
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
				</object>
				</div>
				
				<div class="float_left width_65">
					<div class="ml10">
						<table class="usage_table">
			          	  	<tr>
								<th rowspan="4" id='grid0'></th>
								<td class="value_red" id='grid1'></td>
							</tr>
							<tr>
								<td id='grid2'></td>
							</tr>
							<tr>
								<td id='grid5'><em class="Trd_normal"></em></td>
							</tr>
							<tr>
								<td id='grid6'><em class="Tbu_normal"></em></td>
							</tr>
							<tr>
								<th class="last" id='grid3'></th>
								<td class="last Tgy_bold" id='grid4'></td>
							</tr>
							
						 </table>
					 </div>
				</div>
			</div>
          

			<div class="Bchart clear">
		         <div id="usageTab" class="tab_nobg pl10">
				    <ul>
				        <li><a href="#periodUsage"  id="_periodUsage" ><fmt:message key="aimir.periodUsage"/></a></li>
				        <li><a href="#classificationusage"  id="_classificationusage"><fmt:message key="aimir.classificationusage"/></a></li>
				    </ul>
					<!--  미터유형별 Tab  -->
				    <div id="periodUsage"></div>
				    <!--  지역별 Tab  -->
				    <div id="classificationusage"></div>
				</div>
             </div> 
           </div> 
    </div> 
</div>
</body>
</html>
