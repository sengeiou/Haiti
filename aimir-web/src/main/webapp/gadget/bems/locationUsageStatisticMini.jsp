<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>BEMS-설비별 사용량 모니터링:Mini</title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //플렉스객체 
    var flex;
    var currentDate;
    var supplierId;

    var tmGlobalData;
    var usageGlobalData;
    var locGlobalData;
    var billGlobalData;

//  FusionChart가 정상적으로 동작하지 않을시 debugging용	
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
    				        	onSelect: function(dateText, inst) {  modifyDate(dateText, inst);},
    				        	buttonImageOnly: true});

    						 $("#dailyStartDate").val($.datepicker.formatDate('yymmdd', new Date()));
    						 modifyDate($("#dailyStartDate").val(), '');
    						 currentDate = $("#dailyStartDate").val();
    					}
    				}
    		}); 

    	 $(function() { $('#dailyLeft')      .bind('click',  function(event) { dailyArrow($('#dailyStartDate').val(),-1); } ); });
    	 $(function() { $('#dailyRight')     .bind('click',  function(event) { dailyArrow($('#dailyStartDate').val(),1); } ); });

     	 $("#locMini").tabs();
    	 
          // 브라우저별로 플렉스객체를 초기화한다.        
       	 flex = getFlexObject('locationUsageMonitoringMini');
        $('#periodType').selectbox();    
    });


    // datepicker로 선택한 날짜의 포맷 변경
    function modifyDate(setDate, inst){  
    	setDate.replace('/','').replace('/','');
    	
        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:supplierId}
                ,function(json) {
                
                    $("#dailyStartDate").val(json.localDate);
                    $("#dailyStartDate").trigger('change');
                });
    }
    
    /**
     * 일별 화살표처리
     */
    function dailyArrow(bfDate,val){
       
        if(bfDate==currentDate && val>0){
           return;
        }
        bfDate = bfDate.replace('/','').replace('/','');
       
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:bfDate,addVal:val,supplierId:supplierId}
                ,function(json) {
                    
                    $('#dailyStartDate').val(json.searchDate);                    
                });
    }

    /**
     * 공통 send 거래
     * 개별 화면에서 각각 구현해야한다.
     * 조회버튼클릭시,조회데이터 변경시 최종적으로 호출하게 된다.
     */
    function send(){
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

        
        fmtMessage[0] = '<fmt:message key="aimir.temperature"/>';                 //온도(℃)
        fmtMessage[1] = '<fmt:message key="aimir.locationUsage.innerTemperature"/>';                 // 내기 온도
        fmtMessage[2] = '<fmt:message key="aimir.locationUsage.outerTemperature"/>';                 // 외기 온도
        fmtMessage[3] = '<fmt:message key="aimir.locationUsage.innerHumidity"/>';                 //내기 습도
        fmtMessage[4] = '<fmt:message key="aimir.locationUsage.humidity"/>';                 //습도(%)
        fmtMessage[5] = '<fmt:message key="aimir.locationUsage.outerHumidity"/>';                 //외기 습도
        fmtMessage[6] = '<fmt:message key="aimir.locationUsage.usage"/>';                 //사용량
        fmtMessage[7] = '<fmt:message key="aimir.locationUsage.usage"/>'+' [kgOE]';                 //사용량(w/h)
        fmtMessage[8] = '<fmt:message key="aimir.locationUsage.energyUsage"/>';                 //전기 사용량
        fmtMessage[9] = '<fmt:message key="aimir.locationUsage.gasUsage"/>';                 //가스 사용량
        fmtMessage[10] = '<fmt:message key="aimir.locationUsage.waterUsage"/>';                 //수도 사용량
        fmtMessage[11] = '<fmt:message key="aimir.co2formula2"/>';                 //탄소 배출량(kg)
        fmtMessage[12] = '<fmt:message key="aimir.comparison"/>';                 //비교
        fmtMessage[13] = '<fmt:message key="aimir.locationUsage.pay"/>';                 //요금
        fmtMessage[14] = '<fmt:message key="aimir.locationUsage.payUnit"/>';                 //요금(원)
        fmtMessage[15] = '<fmt:message key="aimir.locationUsage.energyPay"/>';                 //전기 요금
        fmtMessage[16] = '<fmt:message key="aimir.locationUsage.gasPay"/>';                 //가스 요금
        fmtMessage[17] = '<fmt:message key="aimir.locationUsage.waterPay"/>';                 //수도 요금
        fmtMessage[18] = '<fmt:message key="aimir.period"/>';                 				//주기 
        fmtMessage[19] = '<fmt:message key="aimir.locationUsage.day"/>';                 //일
        fmtMessage[20] = '<fmt:message key="aimir.locationUsage.week"/>';                 //주
        fmtMessage[21] = '<fmt:message key="aimir.locationUsage.month"/>';                 //월
        fmtMessage[22] = '<fmt:message key="aimir.quarter"/>';                 //분기
        fmtMessage[23] = '<fmt:message key="aimir.locationUsage.search"/>';                 //조회
        fmtMessage[24] = '<fmt:message key="aimir.locationUsage.lastYearCompare"/>';                 //전년도 비교
        fmtMessage[25] = '<fmt:message key="aimir.locationUsage.won"/>';                 //원        
        fmtMessage[26] = '<fmt:message key="aimir.facilityMgmt.heat"/>';                 //열량 사용량      
        fmtMessage[27] = '<fmt:message key="aimir.locationUsage.heatPay"/>';                 //열량 요금    

        
        return fmtMessage;
    }

    /**
     * Flex 에서 조회조건을 조회하기위한 함수.
     */
    function getParams(){
        var condArray = [];
        condArray[0] = $('#periodType').val();
        condArray[1] = $('#dailyStartDate').val();
        return condArray;
    }
    /**
    function isVaildDate(date){
    	
        if(date.length!=8){
          
           return false;
         }
        var yy= date.substring(0,4);
        var mm = date.substring(4,6);
        var dd = date.substring(6,8);
        var dateVar = new Date(yy,mm,dd );
       
        return (dateVar.getFullYear()==yy && dateVar.getMonth()==mm && dateVar.getDate()==dd) ? true : false;
    }
    **/
    
    function search(){

    	flex.getUsage();    	
    }
    
    window.onresize = search;

    /* 검색 주기가 '월'인 경우 fusionChart의 x축 라벨에 대해 회전 속성을 적용한다. */
    function rotateLabelStatement() {
    	var str = "";
    	
     	if ( $('#periodType').val() === '4' ) {
     		str = "labelDisplay='Rotate' slantLabels='1' ";
     	}
    	return str;
    }
    
    /* 검색 주기에 따라서  온습도 그래프의 하단 마진 값을 설정한다. */
    function setTmChartBottomMargin() {
    	var margin = "chartBottomMargin='0' ";
    	
    	// 일: 0, 주: 1, 월:4, 분기: 9
    	if ( $('#periodType').val() === '1') {
    		margin = "chartBottomMargin='5' ";
    	}
    	
    	return margin;
    }
    
    function setGraph(tmData,usageData,billData){
		
		
    	if(!$('#tmChartDiv').is(':visible')) {
            return;
       }
    	   /* 온습도 Chart  */
    	    if(tmData){
    	    	 tmData= "<chart chartLeftMargin='10' "
    	    /*	 + rotateLabelStatement()*/
    	    	+ "chartLeftMargin='10' "
                + "chartRightMargin='0' "
    			+ "chartTopMargin='10' "
    			+ setTmChartBottomMargin()
    			+ "canvasBorderColor='929292' "
				+ "canvasBorderThickness='1' "
				+ "rotateXAxisName='0' "
				+ "labelDisplay='NONE' "
    			+ fChartStyle_Common
                + fChartStyle_Font
                + fChartStyle_MSCombiDY2D_nobg
    			+ tmData;
        	    tmGlobalData= tmData;
    	    }   	    
     	    
            var tmChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "tmChartId",($('#tmChartDiv').width()),  120, "0", "0");
            
            if(tmData)
            	tmChart.setDataXML(tmData);
            else{
            	tmChart.setDataXML(tmGlobalData);
            }
			tmChart.setTransparent("transparent");
			tmChart.render("tmChartDiv");
            /* 사용량/탄소배출량 Chart  */
            if(usageData){
            	 usageData= "<chart chartLeftMargin='0' "
                	/*+ rotateLabelStatement()*/
            	+ "chartRightMargin='0' "
    			+ "chartTopMargin='10' "
    			+ "chartBottomMargin='5' "
    			+ "rotateXAxisName='0' "
				+ "labelDisplay='NONE' "
				+ "useEllipsesWhenOverFlow='0' "
    			+ fChartStyle_Common
                + fChartStyle_Font
    			+ fChartStyle_StColumn3D_nobg
    			+ usageData;
    	    	usageGlobalData= usageData;
    	    }
            
            var usageChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3DLineDY.swf", "usageChartId", 373,  160, "0", "0");
            
            if(usageData){
            	usageChart.setDataXML(usageData);
            }else{
            	usageChart.setDataXML(usageGlobalData);
            }
            usageChart.setTransparent("transparent");
            usageChart.render("usage");
            
            /* 요금 Chart  */
            if(billData){
            	 billData= "<chart chartLeftMargin='0' "
             	/*+ rotateLabelStatement()*/
            	+ "chartRightMargin='0' "
    			+ "chartTopMargin='10' "
    			+ "chartBottomMargin='5' "
    			+ "rotateXAxisName='0' "
				+ "labelDisplay='NONE' "
				+ "useEllipsesWhenOverFlow='0' "
    			+ fChartStyle_Common
                + fChartStyle_Font
    			+ fChartStyle_StColumn3D_nobg
    			+ billData;
            	billGlobalData= billData;
    	    }
            
            var billChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "billChartId", 373,  160, "0", "0");
            
            if(billData) {
            	billChart.setDataXML(billData);
            }else{
            	billChart.setDataXML(billGlobalData);
            }
            billChart.setTransparent("transparent");
            billChart.render("bill");
    }

    function setGraph1(locData){
		
    	/* 사용량 비교 Chart */
        if(locData){
        	locData= "<chart chartLeftMargin='0' "
            + "chartRightMargin='0' "
			+ "chartTopMargin='10' "
			+ "chartBottomMargin='0' "	
			+ "useEllipsesWhenOverFlow='0' "
			+ fChartStyle_Common
            + fChartStyle_Font
			+ fChartStyle_StColumn3D_nobg
			+ locData;
	    	locGlobalData= locData;
	    }
        var locChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3DLineDY.swf", "locChartId", 373,  160, "0", "0");

        if(locData){
        	locChart.setDataXML(locData);
        }else{
        	locChart.setDataXML(locGlobalData);
        }
        locChart.setTransparent("transparent"); 
        locChart.render("compare");
        
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
                  <option value="0"><fmt:message key="aimir.locationUsage.day"/></option>
                  <option value="1"><fmt:message key="aimir.locationUsage.week"/></option>
                  <option value="4"><fmt:message key="aimir.locationUsage.month"/></option>
                  <option value="9"><fmt:message key="aimir.quarter"/></option>
                </select>
            </li> 
           	<li><button id="dailyLeft" type="button" class="backicon srrow" ></button></li>
		   	<li><input id="dailyStartDate" type="text" readonly="readonly" style="width:70px"></li>
		   	<li><button id="dailyRight" type="button" class="nexticon srrow" ></button></li>
			<li><em class="bems_button"><a href="javascript:search()" id="dailySearch"><fmt:message key="aimir.locationUsage.search"/></a></em></li>           
        </ul>
       <!-- 주기 및 날짜  (E) -->
       </div>
     
			    
	   <div class="Bchart clear"> 
           <div class="mtrl10" style="height:120px">
                <div class="width_35 float_left">
					<object id="locationUsageMonitoringMiniEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="120">
		            <param name="movie" value="${ctx}/flexapp/swf/bems/locationUsageChart_fusion.swf" />
		            <param name="wmode" value="opaque">
		            <!--[if !IE]>-->
		            <object id="locationUsageMonitoringMiniOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/locationUsageChart_fusion.swf" width="100%" height="120">
		            <param name="wmode" value="opaque">
		            <!--<![endif]-->
		            <div>
		                <h4>Content on this page requires a newer version of Adobe Flash Player.</h4>
		                <p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" width="112" height="33" /></a></p>
		            </div>
		            <!--[if !IE]>-->
		            </object>
		            <!--<![endif]-->
		        	</object>
				</div>
               	<div id="tmChartDiv" class="width_65 float_left"></div>
           </div>
          <div class="Bchart clear">
          
          <div id="locMini" class="tab_nobg pl10">
		        <ul>
		            <li><a href="#usage"  id="_usage" ><fmt:message key="aimir.locationUsage.usage"/></a></li>
		            <li><a href="#compare"  id="_compare"><fmt:message key="aimir.comparison"/></a></li>
		            <li><a href="#bill" id="_bill"><fmt:message key="aimir.locationUsage.pay"/></a></li>
		        </ul>
				<!--  미터유형별 Tab  -->
		        <div id="usage"></div>
		
		        <!--  지역별 Tab  -->
		        <div id="compare"></div>
		
		        <!-- 통신상태별 Tab -->
		        <div id="bill"></div>
		    </div>
           
	       </div> 
	    </div>
			
      
    </div> 
</div>
</body>
</html>
