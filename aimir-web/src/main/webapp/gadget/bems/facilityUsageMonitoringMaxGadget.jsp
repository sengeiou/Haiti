<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>BEMS-설비별 사용량 모니터링:Max</title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
<style type="text/css" media="screen">
	div#wrapper div#bm_max div.wrap {
		position: relative;
		width: 1241px;
	}
	
	div#bm_max_content div.half_box label.subtitle.mb10 {
		margin-bottom: 5px;
	}

	div#bm_max_content div.half_box {
		padding-bottom: 0px;
	}	
	
	div.wrap div.float_left {
		width: 960px;	
	}
	
	div#bm_max_content {
		position: relative;
		margin-left: 0px;
	}
	
	div#bm_max_content table.data_table{
		margin-top: 5px;
	}
	
	div#bm_max_content label.subtitle.mb10 span.label_tit {
		margin: 0px;
	}
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //플렉스객체 
    var flex;
    var supplierId;
    var classifyGlobalData;
    var dailyGlobalData;
    var weeklyGlobalData;
    var monthlyGlobalData;
    var quaterlyGlobalData;
    var codeId;
    var miniGridArr;
    var dailyArr;
    var weeklyArr;
    var monthlyArr;
    var quaterlyArr;

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
//     	 $('#periodType').selectbox(); 
    	 
        // 브라우저별로 플렉스객체를 초기화한다.
        flex = getFlexObject('facilityUsageTree');
//         miniGridFlex= getFlexObject('miniGrid');
//         dailyGridFlex = getFlexObject('dailyGrid');
//         weeklyGridFlex = getFlexObject('weeklyGrid');
//         monthlyGridFlex = getFlexObject('monthlyGrid');
//         quaterlyGridFlex = getFlexObject('quaterlyGrid');
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
        //http or flex Request Send
        if (flex != null) {
            flex.getUsage();
        }
    }

    /**
     * Flex 에서 메세지를 조회하기위한 함수
     */
    function getFmtMessage(){
        var fmtMessage = new Array();

        fmtMessage[0] = '<fmt:message key="aimir.alert"/>';                 // 장애
        fmtMessage[1] = '<fmt:message key="aimir.facilityClassification"/>';//"설비분류";
        fmtMessage[2] = '<fmt:message key="aimir.facilityUsage"/>';//"설비별사용량";
		fmtMessage[3] = '<fmt:message key="aimir.date.today"/>'+' ' +'<fmt:message key="aimir.usage"/>';//"금일사용량";
        fmtMessage[4] = '<fmt:message key="aimir.day"/>'+'<fmt:message key="aimir.usage"/>';//"일 사용량";
        fmtMessage[5] = '<fmt:message key="aimir.week"/>'+'<fmt:message key="aimir.usage"/>';//"주 사용량";
        fmtMessage[6] = '<fmt:message key="aimir.month"/>'+'<fmt:message key="aimir.usage"/>';//"월 사용량";
        fmtMessage[7] = '<fmt:message key="aimir.quarter"/>'+'<fmt:message key="aimir.usage"/>';//"분기 사용량";
        fmtMessage[8] = '<fmt:message key="aimir.daily"/>'+'<fmt:message key="aimir.usage"/>';//"일별 사용량";
        fmtMessage[9] = '<fmt:message key="aimir.electricity"/>';//"전기";
        fmtMessage[10] = '<fmt:message key="aimir.gas"/>';//"가스";
        fmtMessage[11] = '<fmt:message key="aimir.water"/>';//"수도";
        fmtMessage[12] = '<fmt:message key="aimir.co2formula2"/>';//"Co2배출량";
        fmtMessage[13] = '<fmt:message key="aimir.usage"/>'+' [kgOE]';//"사용량(TOE)";
        fmtMessage[14] = '<fmt:message key="aimir.min"/>';//"최소";
        fmtMessage[15] = '<fmt:message key="aimir.max"/>';//"최대";
        fmtMessage[16] = '<fmt:message key="aimir.date.yesterday"/>';//"전일";
        fmtMessage[17] = '<fmt:message key="aimir.date.today"/>';//"금일";
        fmtMessage[18] = '<fmt:message key="aimir.date.lastweek"/>';//"전주";
        fmtMessage[19] = '<fmt:message key="aimir.date.thisweek"/>';//"금주";
        fmtMessage[20] = '<fmt:message key="aimir.lastyear"/>';//"금주";;//"전년";
        fmtMessage[21] = '<fmt:message key="aimir.date.thisYear"/>';//"당해";
        fmtMessage[22] = '<fmt:message key="aimir.lastyear"/>';//"전년";
        fmtMessage[23] = '<fmt:message key="aimir.date.thisYear"/>';//"당해";

        fmtMessage[24] = '<fmt:message key="aimir.facilityUsageMonitoring"/>';//"설비별 사용량 모니터링";
        fmtMessage[25] = '<fmt:message key="aimir.searchDate"/>';//조회기준일자
        fmtMessage[26] = '<fmt:message key="aimir.all"/>';//전체
        fmtMessage[27] = '<fmt:message key="aimir.searchPeriod"/>';//조회주기
        fmtMessage[28] = '<fmt:message key="aimir.py.currenttime"/>';//현재시각
        fmtMessage[29] = '<fmt:message key="aimir.usageFee2"/>';//사용요금
        fmtMessage[30] = '<fmt:message key="aimir.hour"/>';//시간
        fmtMessage[31] = '<fmt:message key="aimir.sum"/>';//합계
        fmtMessage[32] = '<fmt:message key="aimir.dayofweek"/>';//요일
        fmtMessage[33] = '<fmt:message key="aimir.month"/>';//월
        fmtMessage[34] = '<fmt:message key="aimir.quarter"/>';//분기

        fmtMessage[35] = '<fmt:message key="aimir.price.unit"/>';//"원";

        fmtMessage[36] = '<fmt:message key="aimir.day.sun"/>'; //"일";
        fmtMessage[37] = '<fmt:message key="aimir.day.mon"/>'; //"월";
        fmtMessage[38] = '<fmt:message key="aimir.day.tue"/>'; //"화";
        fmtMessage[39] = '<fmt:message key="aimir.day.wed"/>'; //"수";
        fmtMessage[40] = '<fmt:message key="aimir.day.thu"/>'; //"목";
        fmtMessage[41] = '<fmt:message key="aimir.day.fri"/>'; //"금";
        fmtMessage[42] = '<fmt:message key="aimir.day.sat"/>'; //"토";
        fmtMessage[43] = '<fmt:message key="aimir.hour2"/>';//시
        fmtMessage[44] = '<fmt:message key="aimir.heatmeter"/>'; //열량 
        
        return fmtMessage;
    }

    /**
     * Flex 에서 조회조건을 조회하기위한 함수.
     */
    function getParams(){
        // 검색일만 yyyymmdd로 넘긴다
        return $('#dailyStartDate').val();
    }

    function doPrint(){
        flex.doPrint();
    }

    function setArray(miniG,day,week,month,quater){
    	$('#miniGrid0').html(miniG[2]);  
    	$('#miniGrid1').html(miniG[1]); 
    	$('#miniGrid2').html(miniG[3]);  

    	$('#daily0').html(day[0]);  
    	$('#daily1').html(day[1] +" ("+day[2]+")"+"<br/>"+"<em class='Tbu_normal'>"+day[5]+"</em> : "+day[6]+" <br> <em class='Trd_normal'>"+day[7]+"</em> : "+day[8]);
    	$('#daily2').html(day[3]); 
    	$('#daily3').html(day[4]); 

    	$('#weekly0').html(week[0]);  
    	$('#weekly1').html(week[1] +" ("+week[2]+")"+"<br/>"+"<em class='Tbu_normal'>"+week[5]+"</em> : "+week[6]+" <br> <em class='Trd_normal'>"+week[7]+"</em> : "+week[8]);
    	$('#weekly2').html(week[3]); 
    	$('#weekly3').html(week[4]);


    	$('#monthly0').html(month[0]);  
    	$('#monthly1').html(month[1] +" ("+month[2]+")"+"<br/>"+"<em class='Tbu_normal'>"+month[5]+"</em> : "+month[6]+" <br> <em class='Trd_normal'>"+month[7]+"</em> : "+month[8]);
    	$('#monthly2').html(month[3]); 
    	$('#monthly3').html(month[4]);


    	$('#quaterly0').html(quater[0]);  
    	$('#quaterly1').html(quater[1] +" ("+quater[2]+")"+"<br/>"+"<em class='Tbu_normal'>"+quater[5]+"</em> : "+quater[6]+" <br> <em class='Trd_normal'>"+quater[7]+"</em> : "+quater[8]);
    	$('#quaterly2').html(quater[3]); 
    	$('#quaterly3').html(quater[4]);  		
    	
    }

    function getMiniGridArray(){
        return miniGridArr;
    }

    function getPeriodArray(period){
    	if(period =="daily")
			return dailyArr;
		else if(period =="weekly")
			return weeklyArr;
		else if(period =="monthly")
			return monthlyArr;
		else if(period =="quaterly")
			return quaterlyArr;
        return dailyArr;
    }


    //window.onresize = setGraph;
    
    
    function setGraph(classifyData,dailyData,weeklyData,monthlyData,quaterlyData){    	   
			var columnChartHeight = 252;

			/* 설비별 사용량 (pieChart) */
            if(classifyData){           	
            	classifyData="<chart chartLeftMargin='0' "
                    + "chartRightMargin='0' "
        			+ "chartTopMargin='5' "
        			+ "chartBottomMargin='0' "        			
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Pie3D
                    + "showLegend='1' "
                    + "showValues='0' "
                    + "showLabels='0' "
        			+ "legendShadow='0' "
        			+ classifyData;
            	classifyGlobalData= classifyData;
    	    }
            var classifyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/Doughnut3D.swf", "classifyChartId",($('#classificationusage').width()),  200, "0", "0");            
            if(classifyData)
            	classifyChart.setDataXML(classifyData);
            else{
            	classifyChart.setDataXML(classifyGlobalData);
            }
            classifyChart.setTransparent("transparent");           
            classifyChart.render("classificationusage");
			
            /* 일 사용량 columnChart & lineChart */           
            if(dailyData){
            	dailyData="<chart chartLeftMargin='0' "
                	+ "labelStep='2'"
                	+ "labelDisplay='NONE' "
                    + "chartRightMargin='0' "
        			+ "chartTopMargin='10' "
        			+ "chartBottomMargin='0' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + dailyData;
            	dailyGlobalData= dailyData;
    	    }
            var dailyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSStackedColumn2DLineDY.swf", "dailyChartId",($('#dailyChartDiv').width()),  columnChartHeight, "0", "0");            
            if(dailyData)
            	dailyChart.setDataXML(dailyData);
            else{
            	dailyChart.setDataXML(dailyGlobalData);
            }
            dailyChart.setTransparent("transparent");           
            dailyChart.render("dailyChartDiv");
            
            /* 주 사용량 columnChart & lineChart */
            if(weeklyData){
            	weeklyData="<chart chartLeftMargin='0' "
            		+ "chartRightMargin='0' "
        			+ "chartTopMargin='10' "
        			+ "chartBottomMargin='0' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + weeklyData;
            	weeklyGlobalData= weeklyData;
    	    }
            var weeklyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSStackedColumn2DLineDY.swf", "weeklyChartId",($('#weeklyChartDiv').width()),  columnChartHeight, "0", "0");            
            if(weeklyData)
            	weeklyChart.setDataXML(weeklyData);
            else{
            	weeklyChart.setDataXML(weeklyGlobalData);
            }
            weeklyChart.setTransparent("transparent");           
            weeklyChart.render("weeklyChartDiv");

            /* 월 사용량 columnChart & lineChart */           
            if(monthlyData){
            	monthlyData="<chart chartLeftMargin='0' "
                	+ "labelDisplay='NONE' "
            		+ "chartRightMargin='0' "
        			+ "chartTopMargin='10' "
        			+ "chartBottomMargin='0' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ fChartStyle_legendScroll
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + monthlyData;
            	monthlyGlobalData= monthlyData;
    	    }
            var monthlyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSStackedColumn2DLineDY.swf", "monthlyChartId",($('#monthlyChartDiv').width()),  columnChartHeight, "0", "0");            
            if(monthlyData)
            	monthlyChart.setDataXML(monthlyData);
            else{
            	monthlyChart.setDataXML(monthlyGlobalData);
            }
            monthlyChart.setTransparent("transparent");           
            monthlyChart.render("monthlyChartDiv");
            
            /* 분기 사용량 columnChart & lineChart */
            if(quaterlyData){
            	quaterlyData="<chart chartLeftMargin='0' "
                    + "chartRightMargin='0' "
        			+ "chartTopMargin='10' "
        			+ "chartBottomMargin='0' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                	+ quaterlyData;
            	quaterlyGlobalData= quaterlyData;
    	    }
            var quaterlyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSStackedColumn2DLineDY.swf", "quaterlyChartId",($('#quaterlyChartDiv').width()),  columnChartHeight, "0", "0");            
            if(quaterlyData)
            	quaterlyChart.setDataXML(quaterlyData);
            else{
            	quaterlyChart.setDataXML(quaterlyGlobalData);
            }
            quaterlyChart.setTransparent("transparent");           
            quaterlyChart.render("quaterlyChartDiv");

            
    }
    
    //======================================================================================

/*]]>*/
    </script>
</head>

<body class="bg">
<div id="wrapper">
    <div id="bm_max">
       <!-- 주기 및 날짜 (S) -->
      	<div class="tapBg ptb5">
        <ul class="noTapSearch">
        	<li class="tit_default pr5"><fmt:message key="aimir.bems.label.searchDate"/></li>
<%--             <li class="tit_default"><fmt:message key="aimir.locationUsage.term"/></li> --%>
<!--             <li> -->
<!--                 <select id="periodType" style="width:60px"> -->
<%--                   <option value="1"><fmt:message key="aimir.locationUsage.day"/></option> --%>
<%--                   <option value="3"><fmt:message key="aimir.locationUsage.week"/></option> --%>
<%--                   <option value="4"><fmt:message key="aimir.locationUsage.month"/></option> --%>
<%--                   <option value="9"><fmt:message key="aimir.quarter"/></option> --%>
<!--                 </select> -->
<!--             </li>  -->
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
        <div class="wrap" >
          <div class="lnb">
	          <div>
		          <object id="facilityUsageTreeEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="200" codebase="http://fpdownload.macromedia.com/get/flashplayr/current/swflash.cab">
					<param name="movie" value="${ctx}/flexapp/swf/bems/facilityUsageMonitoring_tree.swf">
					<param name="quality" value="high">
					<param name="wmode" value="opaque">
					<param name="swfversion" value="9.0.45.0">
					<!-- This param tag prompts users with Flash Player 6.0 r65 and higher to download the latest version of Flash Player. Delete it if you don’t want users to see the prompt. -->
					<param name="expressinstall" value="Scripts/expressInstall.swf">
					<!-- Next object tag is for non-IE browsers. So hide it from IE using IECC. -->
					<!--[if !IE]>-->
					<object id="facilityUsageTreeOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/facilityUsageMonitoring_tree.swf" width="100%" height="200">
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
	         
	          <div id="classificationusage"></div>
	          
          	  <div class="float_right mb2"><span class="icon_clock"></span><span id="currDate1"></span></div>
          	  
          	  <div class="clear">
	          	  <table class="info_table">
	          	  	<tr>
						<th rowspan="2"><fmt:message key="aimir.date.today"/><fmt:message key="aimir.usage"/></th>
						<td class="value_red" id="miniGrid0"></td>
					</tr>
					<tr>
						<td  id="miniGrid1"></td>
					</tr>
					<tr>
						<th rowspan="1" class="last"><fmt:message key="aimir.co2formula2"/></th>
						<td class="last Tgy_bold"  id="miniGrid2"></td>
					</tr>
					
				 </table>
          	  </div>           
          </div>
         
          <div class="float_left">
          
          <div id="bm_max_content">
          		<div class="half_box" >
          			<div class="sub_content mr20">
          				<label class="subtitle mb10"><span class="label_tit"></span><fmt:message key="aimir.day"/> <fmt:message key="aimir.usage"/></label>
          				<div id="dailyChartDiv" class="w_auto clear"></div>
          				<div class="w_auto mgn_side10">
          					<table class="data_table">
          						<tr>
          							<th id="daily0"></th>
          							<td id="daily1" class="Tgy_bold"></td>
          						</tr>
          						<tr>
          							<th id="daily2" ></th>
          							<td id="daily3" class="last"></td>
          						</tr>
          					</table>
          				</div>
           			</div>
          		</div>
          		
          		<div class="half_box">
          			<div class="sub_content mr20">
          				<label class="subtitle mb10"><span class="label_tit"></span><fmt:message key="aimir.week"/> <fmt:message key="aimir.usage"/></label>
          				<div id="weeklyChartDiv" class="w_auto clear"></div>
          				<div class="w_auto mgn_side10">
          					<table class="data_table">
          						<tr>
          							<th id="weekly0"></th>
          							<td id="weekly1" class="Tgy_bold"></td>
          						</tr>
          						<tr>
          							<th id="weekly2"></th>
          							<td id="weekly3" class="last"></td>
          						</tr>
          					</table>
          				</div>
           			</div>
          		</div>
          		<div class="clear" ></div>		
          </div>
          
          
          <div id="bm_max_content" >
          		<div class="half_box">
          			<div class="sub_content mr20">
          				<label class="subtitle mb10"><span class="label_tit"></span><fmt:message key="aimir.month"/> <fmt:message key="aimir.usage"/></label>
          				<div id="monthlyChartDiv" class="w_auto clear"></div>
          				<div class="w_auto mgn_side10">
          					<table class="data_table">
          						<tr>
          							<th id="monthly0"></th>
          							<td  id="monthly1" class="Tgy_bold"></td>
          						</tr>
          						<tr>
          							<th id="monthly2"></th>
          							<td  id="monthly3" class="last"></td>
          						</tr>
          					</table>
          				</div>
          			</div>
          		</div>
          		          		
          		<div class="half_box">
          			<div class="sub_content mr20">
          				<label class="subtitle mb10"><span class="label_tit"></span><fmt:message key="aimir.quarter"/> <fmt:message key="aimir.usage"/></label>
          				<div id="quaterlyChartDiv" class="w_auto clear"></div>
          				<div class="w_auto mgn_side10">
          					<table class="data_table">
          						<tr>
          							<th id="quaterly0"></th>
          							<td id="quaterly1" class="Tgy_bold"></td>
          						</tr>
          						<tr>
          							<th id="quaterly2"></th>
          							<td id="quaterly3" class="last"></td>
          						</tr>
          					</table>
          				</div>
          			</div>
          		</div>
          		<div class="clear" ></div>		
          </div>  
          
          </div>
           
          <div class="clear"></div>
    	</div>
    </div>
</div>
</body>
</html>
