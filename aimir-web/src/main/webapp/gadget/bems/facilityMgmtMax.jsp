<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
	<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/FusionCharts.js"></script>
	<script type="text/javascript" >
		var supplierObj;
		var sId;
		var fmtMessage = new Array();
		var usageGlobalData;
		var dailyGlobalData;
		var weeklyGlobalData;
		var monthlyGlobalData;
		var yearlyGlobalData;
		function init() {
			
			var supplierId;
			$.getJSON('${ctx}/common/getUserInfo.do',
	                function(json) {
	                    if(json.supplierId != ""){
	                        supplierId = json.supplierId;
	                        if(supplierId != "") {
	                        	sId= supplierId;
	            				getSupplier(supplierId);
	            			}
                      	
	                    }
	                }
	        );		
		}
		
		function getEndDeviceLog(locationId,endDeviceId){
			getFlexObject("grid").getEndDeviceLog(locationId,endDeviceId);		    	
		   
		}


		function getEndDeviceByLocationId(locationId,endDeviceId){
			getFlexObject("grid").getEndDeviceByLocationId(locationId,endDeviceId);		    	
		   
		}

		function getEndDeviceConditionByLocationId(locationId,endDeviceId){
			getFlexObject("grid").getEndDeviceConditionByLocationId(locationId,endDeviceId);		    	
		   
		}

		function getChartLocation(locationId,endDeviceId){
			getFlexObject("tree").getChartLocation(locationId,endDeviceId);		    	
		   
		}

		function getMeteringChart(endDeviceId){
			
			getFlexObject("tree").getMeteringChart(endDeviceId);		    	
		  
		}
				
		// 공급사 선택 시 정보를 로딩한다.
		function getSupplier(supplierId){ 
			if (supplierId != null) {
				sId = supplierId;
				sLoad = true;
			}
			$.getJSON('${ctx}/gadget/system/getSupplierList.do', {supplierId: sId},
				function(json){
					supplierObj = json.supplier;
			});
		}	

		// 메세지 처리 
		function getFmtMessage(){	
			fmtMessage[0]  = '<fmt:message key="aimir.bems.facilityMgmt.add"/>'; //"추 가";
			fmtMessage[1]  = '<fmt:message key="aimir.bems.facilityMgmt.update"/>';        //"수 정";
			fmtMessage[2]  = '<fmt:message key="aimir.bemsfacilityMgmt.delete"/>';     //"삭제";
			fmtMessage[3]  = '<fmt:message key="aimir.facility.number"/>';     //"이동";
			fmtMessage[4]  = '<fmt:message key="aimir.bems.facilityMgmt.count"/>';   //설비(대수);

			fmtMessage[5]  = '<fmt:message key="aimir.bems.facilityMgmt.operation"/>';     //운영;
			fmtMessage[6]  = '<fmt:message key="aimir.stop"/>';     //정지;
			fmtMessage[7]  = '<fmt:message key="aimir.unknown"/>';     //확인불가;
			fmtMessage[8]  = '<fmt:message key="aimir.bems.facilityMgmt.location"/>';     // 위치
			fmtMessage[9]  = '<fmt:message key="aimir.bems.facilityMgmt.kind"/>';     // 종류
			
			fmtMessage[10]  = '<fmt:message key="aimir.bems.facilityMgmt.name"/>';    //설비명;
			fmtMessage[11]  = '<fmt:message key="aimir.afterChange"/>';      //변경후;
			fmtMessage[12]  = '<fmt:message key="aimir.facilityMgmt.energy"/>';      //전기 사용량;
			fmtMessage[13]  = '<fmt:message key="aimir.facilityMgmt.water"/>';      //수도 사용량;
			fmtMessage[14]  = '<fmt:message key="aimir.facilityMgmt.gas"/>';     //가스 사용량;
			fmtMessage[15]  = '<fmt:message key="aimir.facilityMgmt.heat"/>';     //열량 사용량;
			fmtMessage[16]  = '<fmt:message key="aimir.bems.facilityMgmt.changeDate"/>';     //일시;
			fmtMessage[17]  = '<fmt:message key="aimir.facilityMgmt.operating.status"/>';     //설비 운영 상태;
			fmtMessage[18]  = '<fmt:message key="aimir.facilityMgmt.operating.status.history"/>';     //설비 운영 상태 이력;	
			fmtMessage[19]  = '<fmt:message key="aimir.facilityMgmt.operating.status.chart"/>';     //설비 운영 상태 분류 챠트	
		   	
			
			fmtMessage[20]  ='<fmt:message key="aimir.facilityMgmt.situation"/>';     //'설비현황';
			fmtMessage[21]  ='<fmt:message key="aimir.bems.facilityMgmt.kind"/>';     //'종류';
			fmtMessage[22]  ='<fmt:message key="aimir.equipvendor"/>';     //'제조사';
			fmtMessage[23]  ='<fmt:message key="aimir.model"/>';     //'모델';
			fmtMessage[24]  ='<fmt:message key="aimir.name"/>';     //'이름';						
			fmtMessage[25]  ='<fmt:message key="aimir.installdate"/>';     //'설치일';		
			fmtMessage[26]  ='<fmt:message key="aimir.facilityMgmt.manufactureDate"/>';     //'제조일';		
			fmtMessage[27]  ='<fmt:message key="aimir.facilityMgmt.powerConsumption"/>';     //'소비전력';		
			fmtMessage[28]  ='<fmt:message key="aimir.facilityMgmt.control"/>';     //'제어';		
			fmtMessage[29]  ='<fmt:message key="aimir.facilityMgmt.controller"/>';     //'제어기';
			fmtMessage[30]  ='<fmt:message key="aimir.changehistory"/>';     //'변경이력'; 
			fmtMessage[31]  ='<fmt:message key="aimir.beforeChange"/>';     //'변경전';
			fmtMessage[32]  ='<fmt:message key="aimir.state"/>';     //'상태';


			fmtMessage[33]  ='<fmt:message key="aimir.facilityMgmt.usagechart"/>';     //설비 사용량 챠트
			fmtMessage[34]  ='<fmt:message key="aimir.date.yesterday"/>';     //전일
			fmtMessage[35]  ='<fmt:message key="aimir.usage"/>';     //사용량
			fmtMessage[36]  ='<fmt:message key="aimir.today"/>';     //당일
			fmtMessage[37]  ='<fmt:message key="aimir.date.lastweek"/>';     //전주
			fmtMessage[38]  ='<fmt:message key="aimir.date.thisweek"/>';     //금주
			fmtMessage[39]  ='<fmt:message key="aimir.facilityMgmt.beforeMonth"/>';     //전월
			fmtMessage[40]  ='<fmt:message key="aimir.thismonth"/>';     //당월
			fmtMessage[41]  ='<fmt:message key="aimir.lastyear"/>';     //전년
			fmtMessage[42]  ='<fmt:message key="aimir.year1"/>';     //금년			
			fmtMessage[43]  ='<fmt:message key="aimir.facilityMgmt.dailyUsage"/>';     //일간 사용량		
			fmtMessage[44]  ='<fmt:message key="aimir.facilityMgmt.weeklyUsage"/>';     //주간 사용량		
			fmtMessage[45]  ='<fmt:message key="aimir.facilityMgmt.monthlyUsage"/>';     //월간 사용량		
			fmtMessage[46]  ='<fmt:message key="aimir.facilityMgmt.yearlyUsage"/>';     //년간 사용량

			fmtMessage[47]  ='<fmt:message key="aimir.energymeter"/>';     //전기
			fmtMessage[48]  ='<fmt:message key="aimir.gas"/>';     //가스
			fmtMessage[49]  ='<fmt:message key="aimir.water"/>';     //수도
			fmtMessage[50]  ='<fmt:message key="aimir.heatmeter"/>';     //열량
			fmtMessage[51]  ='<fmt:message key="aimir.locationUsage.lastTerm"/>';     //전주기
			fmtMessage[52]  ='<fmt:message key="aimir.locationUsage.now"/>';     //현재					
			return fmtMessage;
		}

		 window.onresize = setGraph;setTermGraph;
		 function setGraph(usageData){

		    	
		            if(usageData){
		            	 usageData= "<chart chartLeftMargin='0' "
		                + "chartRightMargin='0' "
		    			+ "chartTopMargin='10' "
		    			+ "chartBottomMargin='0' "
		    			+ fChartStyle_Common
		                + fChartStyle_Font
		    			+ fChartStyle_StColumn3D_nobg
		    			+ usageData;
		    	    	usageGlobalData= usageData;
		    	    }
		            var usageChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/StackedColumn3D.swf", "usageChartId",($('#usage').width()),  200, "0", "0");            
		            if(usageData)
		            	usageChart.setDataXML(usageData);
		            else{
		            	usageChart.setDataXML(usageGlobalData);
		            }
		            usageChart.setTransparent("transparent");           
		            usageChart.render("usage");
		            
		    }

		 function setTermGraph(dailyData,weeklyData,monthlyData,yearlyData){	

			
	            if(dailyData){
	            	dailyData= "<chart chartLeftMargin='0' "
	                + "chartRightMargin='0' "
	    			+ "chartTopMargin='10' "
	    			+ "chartBottomMargin='0' "
	    			+ fChartStyle_Common
	                + fChartStyle_Font
	    			+ fChartStyle_StColumn3D_nobg
	    			+ dailyData;
	            	 dailyGlobalData= dailyData;
	    	    }
	            var dailyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "dailyChartId",($('#daily').width()),  180, "0", "0");            
	            if(dailyData)
	            	dailyChart.setDataXML(dailyData);
	            else{
	            	dailyChart.setDataXML(dailyGlobalData);
	            }
	            dailyChart.setTransparent("transparent");           
	            dailyChart.render("daily");
	       	
	            if(weeklyData){
	            	weeklyData= "<chart chartLeftMargin='0' "
	                + "chartRightMargin='0' "
	    			+ "chartTopMargin='10' "
	    			+ "chartBottomMargin='0' "
	    			+ fChartStyle_Common
	                + fChartStyle_Font
	    			+ fChartStyle_StColumn3D_nobg
	    			+ weeklyData;
	            	weeklyGlobalData= weeklyData;
	    	    }
	            var weeklyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "weeklyChartId",($('#weekly').width()),  180, "0", "0");            
	            if(weeklyData)
	            	weeklyChart.setDataXML(weeklyData);
	            else{
	            	weeklyChart.setDataXML(weeklyGlobalData);
	            }
	            weeklyChart.setTransparent("transparent");           
	            weeklyChart.render("weekly");
	           

	            if(monthlyData){
	            	monthlyData= "<chart chartLeftMargin='0' "
	                + "chartRightMargin='0' "
	    			+ "chartTopMargin='10' "
	    			+ "chartBottomMargin='0' "
	    			+ fChartStyle_Common
	                + fChartStyle_Font
	    			+ fChartStyle_StColumn3D_nobg
	    			+ monthlyData;
	            	monthlyGlobalData= monthlyData;
	    	    }
	            var monthlyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "monthlyChartId",($('#monthly').width()),  180, "0", "0");            
	            if(monthlyData)
	            	monthlyChart.setDataXML(monthlyData);
	            else{
	            	monthlyChart.setDataXML(monthlyGlobalData);
	            }
	            monthlyChart.setTransparent("transparent");           
	            monthlyChart.render("monthly");
	            
	            if(yearlyData){
	            	yearlyData= "<chart chartLeftMargin='0' "
	                + "chartRightMargin='0' "
	    			+ "chartTopMargin='10' "
	    			+ "chartBottomMargin='0' "
	    			+ fChartStyle_Common
	                + fChartStyle_Font
	    			+ fChartStyle_StColumn3D_nobg
	    			+ yearlyData;
	            	yearlyGlobalData= yearlyData;
	    	    }
	            var yearlyChart = new FusionCharts("${ctx}/flexapp/swf/fcChart/MSColumn3D.swf", "yearlyChartId",($('#yearly').width()),  180, "0", "0");            
	            if(yearlyData)
	            	yearlyChart.setDataXML(yearlyData);
	            else{
	            	yearlyChart.setDataXML(yearlyGlobalData);
	            }
	            yearlyChart.setTransparent("transparent");           
	            yearlyChart.render("yearly");
	            
	    }
	</script>
</head>

<body  onload="init();" class="bg">
<div id="wrapper">
  <div id="bm_max">  
    <div class="wrap">
      <div class="lnb">
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="500" id="treeEx">
		<param name='wmode' value='transparent' />
		<param name="movie" value="${ctx}/flexapp/swf/bems/facilityMgmtMini_tree.swf" />
		<!--[if !IE]>-->
		<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/facilityMgmtMini_tree.swf" width="100%" height="500" id="treeOt">
		<!--<![endif]-->
		<!--[if !IE]>-->
		</object>
		<!--<![endif]-->
		</object>
       </div>
     </div>
     
     <div id="bm_max_content">
     	<div class="sub_content mr20">
	        <label class="subtitle mb10 w_auto"><span class="label_tit"></span><fmt:message key="aimir.facilityMgmt.operating.status.chart"/></label>
	        <div id="usage" class="w_auto"></div>
	        <div>
	        	<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="100%" height="190" id="gridEx">
				<param name='wmode' value='transparent' />
				<param name="movie" value="${ctx}/flexapp/swf/bems/facilityMgmtMax_grid.swf" />
				<!--[if !IE]>-->
				<object type="application/x-shockwave-flash" data="${ctx}/flexapp/swf/bems/facilityMgmtMax_grid.swf" width="100%" height="190" id="gridOt">
				<!--<![endif]-->
				<!--[if !IE]>-->
				</object>
				<!--<![endif]-->
				</object>
	        </div>
	    </div>
	  </div>
	  
	  <div id="bm_max_content">
	  	<div class="sub_content mr20">
	  		<label class="subtitle mb10 width_100"><span class="label_tit"></span><fmt:message key="aimir.facilityMgmt.usagechart"/></label>
	    	<div class="half_box">
	    		<div class="quarter">
		    		<label class="Tbu_normal mb10 w_auto"><fmt:message key="aimir.facilityMgmt.dailyUsage"/></label>
		    		<div id="daily" class="w_auto"></div>
	    		</div> 
	    		<div class="quarter">
	    			<label class="Tbu_normal mb10 w_auto"><fmt:message key="aimir.facilityMgmt.weeklyUsage"/></label>
	    			<div id="weekly" class="w_auto"></div>
	    		</div>
			</div>
			<div class="half_box">
	    		<div class="quarter">
	    			<label class="Tbu_normal mb10 w_auto"><fmt:message key="aimir.facilityMgmt.monthlyUsage"/></label>
	    			<div id="monthly" class="w_auto"></div>
	    		</div> 
	    		<div class="quarter">
	    			<label class="Tbu_normal mb10 w_auto"><fmt:message key="aimir.facilityMgmt.yearlyUsage"/></label>
	    			<div id="yearly" class="w_auto"></div>
	    		</div> 
			</div> 
		</div> 
	</div>
   </div>
</div>

</body>
</html>