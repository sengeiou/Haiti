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
<style type="text/css" media="screen">
	div#wrapper div#bm_max div.wrap {
		position: relative;
		width: 1241px;
	}
	
	div#bm_max_content div.half_box {
 		padding-bottom: 0px; 
	}	
	
	div#bm_max_content {
		position: relative;
		margin-left: 0px;
	}
	
	div.wrap div.lnb {
		margin-top: 20px;
		height: 680px;
	}
	div.wrap div.float_left {
		height: 700px;
	}
	
	div.wrap div.float_left {
		width: 960px;	
	}
	
	
	div#bm_max_content.pr20 {
		margin-top: 30px;
	}
	
	label.subtitle.mb10 {
		float: none;
		display: block;
	}
</style>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    //플렉스객체 
    var flex;
    var currentDate;
    var supplierId;
    var usageGlobalData;
    var tmGlobalData;
    var billGlobalData;
    var locGlobalData;

    var endDeviceId;
    var locationId;
    var rootBoolean;

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
    	
    	
        // 브라우저별로 플렉스객체를 초기화한다.
        flex = getFlexObject('locationUsageGridMax');

        $(function() { $('#periodType')     .bind('change',
                function(event){
                   var selectVal =$('#periodType').val();
                   if(selectVal=='0'){
                	   $('#yearCompare').show();
                	   $('ul.noTapSearch li.pt2.pr5').show();
                   }else if(selectVal=='1'){
                	   $('#yearCompare').show();
                	   $('ul.noTapSearch li.pt2.pr5').show();
                   }else if(selectVal=='4'){
                	   $('#yearCompare').hide();
                	   $('ul.noTapSearch li.pt2.pr5').hide();
                   }else if(selectVal=='9'){
                	   $('#yearCompare').hide();
                	   $('ul.noTapSearch li.pt2.pr5').hide();
                   }
                } 
         ); 
        });

        $('#periodType').selectbox();    
    });
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
     * Flex 에서 조회조건을 조회하기위한 함수.
     */
    function getParams(){
        var condArray = new Array();
        condArray[0] = $('#periodType').val();
        condArray[1] = $('#dailyStartDate').val(); 
        condArray[2] = $('#compare').is(':checked');
        condArray[3] = locationId;
        condArray[4] = rootBoolean;

        return condArray;
    }

    function setTreeUsage(locId,root){

    	locationId= locId;	
    	rootBoolean=root;

    	flex.getUsage();
    	
    }

    function setTreeParam(locId,root){
  
    	locationId= locId;
    	rootBoolean=root;
    }

    /**
     * Flex 에서 메세지를 조회하기위한 함수
     */
    function getFmtMessage(){
        var fmtMessage = new Array();

        fmtMessage[0] = '<fmt:message key="aimir.temperature"/>'+' [℃]';                 //온도(℃)
        fmtMessage[1] = '<fmt:message key="aimir.locationUsage.innerTemperature"/>';                 // 내기 온도
        fmtMessage[2] = '<fmt:message key="aimir.locationUsage.outerTemperature"/>';                 // 외기 온도
        fmtMessage[3] = '<fmt:message key="aimir.locationUsage.innerHumidity"/>';                 //내기 습도
        fmtMessage[4] = '<fmt:message key="aimir.locationUsage.humidity"/>'+' [%]';                 //습도(%)
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
        fmtMessage[18] = '<fmt:message key="aimir.locationUsage.term"/>';                 //주기
        fmtMessage[19] = '<fmt:message key="aimir.locationUsage.day"/>';                 //일
        fmtMessage[20] = '<fmt:message key="aimir.locationUsage.week"/>';                 //주
        fmtMessage[21] = '<fmt:message key="aimir.locationUsage.month"/>';                 //월
        fmtMessage[22] = '<fmt:message key="aimir.quarter"/>';                 //분기
        fmtMessage[23] = '<fmt:message key="aimir.locationUsage.search"/>';                 //조회


        fmtMessage[24] = '<fmt:message key="aimir.locationUsage.lastYearCompare"/>';                 //전년도 비교
        fmtMessage[25] = '<fmt:message key="aimir.locationUsage.won"/>';                 //원
        
        fmtMessage[26] = '<fmt:message key="aimir.locationUsage.usageEmission"/>';                 //사용량/탄소배출량
        fmtMessage[27] = '<fmt:message key="aimir.locationUsage.electricity"/>';                 //전기
        fmtMessage[28] = '<fmt:message key="aimir.locationUsage.gas"/>';                 //가스
        fmtMessage[29] = '<fmt:message key="aimir.locationUsage.water"/>';                 //수도
        fmtMessage[30] = '<fmt:message key="aimir.locationUsage.emission"/>';                 //배출량
        fmtMessage[31] = '<fmt:message key="aimir.locationUsage.lastTerm"/>';                 //전주기
        fmtMessage[32] = '<fmt:message key="aimir.locationUsage.now"/>';                 //현재
        fmtMessage[33] = '<fmt:message key="aimir.locationUsage.usageCompare"/>';                 //사용량 비교        
        fmtMessage[34] = '<fmt:message key="aimir.locationUsage.tempHumidity"/>';                 //온습도       
        fmtMessage[35] = '<fmt:message key="aimir.locationUsage.tempMax"/>';                 //온도최대
        fmtMessage[36] = '<fmt:message key="aimir.locationUsage.tempMin"/>';                 //온도최소
        fmtMessage[37] = '<fmt:message key="aimir.locationUsage.humMax"/>';                 //습도최대
        fmtMessage[38] = '<fmt:message key="aimir.locationUsage.humMin"/>';                 //습도최소       
        fmtMessage[39] = '<fmt:message key="aimir.locationUsage.usageSave"/>';                 //사용절감율
        fmtMessage[40] = '<fmt:message key="aimir.locationUsage.co2"/>';                 //CO2량
        fmtMessage[41] = '<fmt:message key="aimir.locationUsage.co2Save"/>';                 //CO2절감율      
        fmtMessage[42] = '<fmt:message key="aimir.locationUsage.paySave"/>';                 //요금 절감율
        fmtMessage[43] = '<fmt:message key="aimir.locationUsage.co2"/>';                 //탄소 배출량
		fmtMessage[44] = '<fmt:message key="aimir.hour2"/>';//"시";
		fmtMessage[45] = '<fmt:message key="aimir.dayofweek"/>';//"요일";
		fmtMessage[46] = '<fmt:message key="aimir.month"/>';//"월";
		fmtMessage[47] = '<fmt:message key="aimir.quarter"/>';//"분기";
        fmtMessage[48] = '<fmt:message key="aimir.facilityMgmt.heat"/>';                 //열량 사용량      
        fmtMessage[49] = '<fmt:message key="aimir.locationUsage.heatPay"/>';                 //열량 요금    
        fmtMessage[50] = '<fmt:message key="aimir.heatmeter"/>';                 //열량 
      
        return fmtMessage;
    }
	
	function search(){
    	flex.getUsage();
    }
	
	/* 검색주기에 맞게 x축 타이틀의 여백을 조정한다 */
	function setXAxisNamePadding() {
		var str = "";
		var period = $('#periodType').val(); 
		var weekPadding = 10;
		var monthPadding = 0;
		
		/* 검색주기가 '주'인 경우 */
		if ( period === '1' ) {
			str = "xAxisNamePadding='" + weekPadding + "' "; 	
		}
		
		/* 검색주기가 '월'인 경우 */
		if ( period === '4' ) {
			str = "xAxisNamePadding='" + monthPadding + "' ";
		}
		
		return str;
	}
	
	/* 검색주기에 맞게 x축 라벨의 회전여부를 조정한다 */
	function setCategoryLabelRotation() {
		var str = "";
		var period = $('#periodType').val();
		
		/* 검색주기가 '월'인 경우 */
// 		if ( period === '4' ) {
// 			str = "labelDisplay='Rotate' slantLabels='1' ";
// 		} 	
		
		return str;
	}
	
    function setGraph(usageData,tmData,billData){
    		
    		/* 사용량 Chart  */
            if(usageData){
            	usageData ="<chart chartLeftMargin='0' "
                    + setCategoryLabelRotation()
            		+ setXAxisNamePadding()
            		+ "chartRightMargin='0' "
        			+ "chartTopMargin='6' "
        			+ "chartBottomMargin='5' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ "labelDisplay='NONE' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + usageData;
            	usageGlobalData= usageData;
    	    }
            var usageChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSStackedColumn2DLineDY.swf", "usageChartId",($('#usageChartDiv').width()-10),  220, "0", "0");            
            if(usageData)
            	usageChart.setDataXML(usageData);
            else{
            	usageChart.setDataXML(usageGlobalData);
            }
            usageChart.setTransparent("transparent");           
            usageChart.render("usageChartDiv");
            
            /* 온습도 Chart */
            if(tmData){
            	tmData ="<chart chartLeftMargin='0' "
            		+ "numDivLines='0' "
            		+ setCategoryLabelRotation()
            		+ setXAxisNamePadding()
                    + "chartRightMargin='0' "
        			+ "chartTopMargin='6' "
        			+ "chartBottomMargin='5' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ "labelDisplay='NONE' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + tmData;
            	tmGlobalData= tmData;
            }
            
            var tmChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSCombiDY2D.swf", "tmChartId",($('#tmChartDiv').width()-10),  220, "0", "0");            
            if(tmData)
            	tmChart.setDataXML(tmData);
            else{
            	tmChart.setDataXML(tmGlobalData);
            }
            tmChart.setTransparent("transparent");           
            tmChart.render("tmChartDiv");
            
            /* 요금 Chart */
            if(billData){
            	billData ="<chart chartLeftMargin='0' "
            		+ setCategoryLabelRotation()
            		+ setXAxisNamePadding()
                    + "chartRightMargin='0' "
        			+ "chartTopMargin='6' "
        			+ "chartBottomMargin='5' "
        			+ "useRoundEdges='1' "
        			+ "legendPosition='RIGHT' "
        			+ "labelDisplay='NONE' "
        			+ fChartStyle_Common
                    + fChartStyle_Font
                    + fChartStyle_Column2D_nobg
                    + billData;
            	billGlobalData= billData;
    	    }
            var billChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSStackedColumn2D.swf", "billChartId",($('#billChartDiv').width()-10),  220, "0", "0");            
            if(billData)
            	billChart.setDataXML(billData);
            else{
            	billChart.setDataXML(billGlobalData);
            }
            billChart.setTransparent("transparent");           
            billChart.render("billChartDiv");
    }

    function setGraph1(locData){     	   
    	
    	/* 사용량 비교 Chart */
        if(locData){
        	locData ="<chart chartLeftMargin='0' "
                + "chartRightMargin='0' "
    			+ "chartTopMargin='6' "
    			+ "chartBottomMargin='5' "
    			+ "useRoundEdges='1' "
    			+ "legendPosition='RIGHT' "
    			//+ "labelDisplay='WRAP' "
    			+ fChartStyle_Common
                + fChartStyle_Font
                + fChartStyle_Column2D_nobg
                + locData;
        	locGlobalData= locData;
	    }
        var locChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSStackedColumn2DLineDY.swf", "locChartId",($('#locChartDiv').width()-10),  220, "0", "0");            
        if(locData)
        	locChart.setDataXML(locData);
        else{
        	locChart.setDataXML(locGlobalData);
        }
        locChart.setTransparent("transparent");           
        locChart.render("locChartDiv");

     }      
  	
    //======================================================================================

/*]]>*/
    </script>
</head>

<body class="bg">
<div id="wrapper">
    
   <div  id="bm_max">
	<div class="tapBg seachSpace mt10">
		<ul class="noTapSearch block">
			<li class="tit_default pr5"><fmt:message key="aimir.locationUsage.term"/></li>
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
			<li id="yearCompare"><input id="compare" type="checkbox" class="checkbox"></li>
			<li class="pt2 pr5"><fmt:message key="aimir.locationUsage.lastYearCompare"/></li>
			<li><em class="bems_button"><a href="javascript:search()" id="dailySearch"><fmt:message key="aimir.locationUsage.search"/></a></em></li>          
		</ul>
	</div>
	
   	<div class="wrap">
	   <div class="lnb">
	   		<object id="locationUsageMonitoringMaxEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="100%" codebase="http://fpdownload.macromedia.com/get/flashplayr/current/swflash.cab">
	           <param name="movie" value="${ctx}/flexapp/swf/bems/locationUsageTree.swf">
	           <param name="quality" value="high">
	           <param name="wmode" value="opaque">
	           <param name="swfversion" value="9.0.45.0">
	           <!-- This param tag prompts users with Flash Player 6.0 r65 and higher to download the latest version of Flash Player. Delete it if you don’t want users to see the prompt. -->
	           <param name="expressinstall" value="Scripts/expressInstall.swf">
	           <!-- Next object tag is for non-IE browsers. So hide it from IE using IECC. -->
	           <!--[if !IE]>-->
	           <object id="locationUsageMonitoringMaxOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/locationUsageTree.swf" width="100%" height="100%">
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

	  <div class="float_left"> 
	    <div id="bm_max_content" >
       		<div class="half_box">
       			<div class="sub_content mr20">
       				<label class="subtitle mb10"><span class="label_tit"></span><fmt:message key="aimir.locationUsage.usageEmission"/></label>
       				<div id="usageChartDiv"></div>
       			</div>
       		</div>
       		
       		<div class="half_box">
       			<div class="sub_content mr20">
       				<label class="subtitle mb10"><span class="label_tit"></span><fmt:message key="aimir.locationUsage.usageCompare"/></label>
       				<div id="locChartDiv"></div>
       			</div>
       		</div>
       		<div class="clear" ></div>
        </div> 
        
        <div id="bm_max_content" >
      		<div class="half_box">
      			<div class="sub_content mr20">
      				<label class="subtitle mb10"><span class="label_tit"></span><fmt:message key="aimir.locationUsage.pay"/></label>
      				<div id="billChartDiv"></div>
      			</div>
      		</div>
      		
      		<div class="half_box">
      			<div class="sub_content mr20">
      				<label class="subtitle mb10"><span class="label_tit"></span><fmt:message key="aimir.locationUsage.tempHumidity"/></label>
      				<div id="tmChartDiv"></div>
      			</div>
      		</div>      		
      		<div class="clear" ></div>
        </div>
        
        <div id="bm_max_content" class="pr20">
	        <object id="locationUsageGridMaxEx" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="140" codebase="http://fpdownload.macromedia.com/get/flashplayr/current/swflash.cab">
	          <param name="movie" value="${ctx}/flexapp/swf/bems/locationUsageCompareGrid.swf">
	          <param name="quality" value="high">
	          <param name="wmode" value="opaque">
	          <param name="swfversion" value="9.0.45.0">
	          <!-- This param tag prompts users with Flash Player 6.0 r65 and higher to download the latest version of Flash Player. Delete it if you don’t want users to see the prompt. -->
	          <param name="expressinstall" value="Scripts/expressInstall.swf">
	          <!-- Next object tag is for non-IE browsers. So hide it from IE using IECC. -->
	          <!--[if !IE]>-->
	          <object id="locationUsageGridMaxOt" type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/locationUsageCompareGrid.swf" width="100%" height="140">
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
        </div> 
        <div class="clear"></div>
     </div> 
   </div>
</div>
</body>
</html>