<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!--[if lt IE 9]>
	<script src="${ctx}/js/html5shiv.js"></script>
	<![endif]-->
    <title><fmt:message key="gadget.bems.thresh001"/></title>
    <link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css" />
 	<link href="${ctx}/css/bems/imports_style.css" rel="stylesheet" type="text/css" />
    <%@ include file="/gadget/system/preLoading.jsp"%>
</head>
<body>
<div id="wrapper" class="min">
    <!-- 계약전력과 Peak Demand 기본정보 (S) -->
    <div id="container2">
    <div class="tapBg seachSpace">
       <!-- 주기 및 날짜 (S) -->
        <ul class="header">
            <li class="hLeft tit_default"><fmt:message key="aimir.contract.demand.amount"/></li>
            <li class="hLeft">
				<select id="dateTypeList"  style="width:75px;" class="local-selectbox">
				<c:forEach var="combo" items="${combo}">
				<option value="${combo.id}">${combo.name}</option>
				</c:forEach>
				</select>
             </li>              
           	 <li class="hRight mt5"><div id='basisDate'></div></li>
		</ul>
		</div>
       <!-- 주기 및 날짜  (E) -->
		
    </div> 
    <!-- 계약전력과 Peak Demand 기본정보 (E) -->
    
    <!-- 계약전력과 Peak Demand gauge chart (S) -->
    <div class="pt10 pb10">
    	<div id="bldg_peak_gaige">
	        <div class="gaige_value">     
	            <ul>
	                <li>
		                <span class="text">
		                	<fmt:message key="aimir.critical"/>
		                </span>
		                <span>
		                	<input id="threshold3" name="danger" 
		                		type="text" value="0"  maxlength="2" class="input_danger threshold-critical"/>%
		                </span>    
	                	<span id="lv_info_danger">
	                	   	<img id="lamp_danger_on" 
	                	   		src="${ctx}/themes/images/default/setting/ic-warn-on.png" 
	                	   		style="DISPLAY: none;margin-top:4px;">
	                	    <img id="lamp_danger_off" 
	                	    	src="${ctx}/themes/images/default/setting/ic-warn-off.png" 
	                	    	style="DISPLAY: none;margin-top:4px;">
	                    </span>					 
	                </li>
	                <li>
		                <span class="text"><fmt:message key="aimir.warning"/></span>
		                <span>
		                	<input id="threshold2" name="warn" type="text"
		                		value="0" maxlength="2" class="input_warn threshold-warning"/>
		                %</span>
		                <span id="lv_info_warn">
		                	<img id="lamp_warn_on" 
		                		src="${ctx}/themes/images/default/setting/ic-warn-on.png" 
		                		style="DISPLAY: none;margin-top:4px;">
		                	<img id="lamp_warn_off" 
		                		src="${ctx}/themes/images/default/setting/ic-warn-off.png" 
		                		style="DISPLAY: none;margin-top:4px;">
		                </span>
	                </li>
	                <li style="display:none">
	                	<span class="text"><fmt:message key="aimir.normal"/></span>
	                	<span>
	                		<input id="threshold1" name="good" type="text" value="0" 
	                		maxlength="2" class="input_good threshold-good" readonly/>%
	                	</span> 
	                </li>
	            </ul>
	        </div>
         
	        <div id="lv_info_popup" class="alert" style="DISPLAY: none;">
				 <ul>
				    <li class="close"><a href="#">X</a></li>
				 	<li class="tit" id="lv_danger" style="DISPLAY: none;">
				 	 	<fmt:message key="aimir.criticallevel"/>
				 	</li>
				 	<li class="tit" id="lv_warn" style="DISPLAY: none;">
				 	 	<fmt:message key="aimir.warninglevel"/>
				 	</li>
				  	<li class="cont"> 
				  	<input id="lastAmount" class="last" size="6" readonly/>kW
				  	 	<fmt:message key="aimir.using"/>
				  	</li>
				  	<li  class="cont mt10">
				  	 	<span>(<fmt:message key="aimir.contract.demand.amount"/></span>
				  	 	<span><input id="lastPercent" class="last" size="4" readonly/></span>
						<span>% <fmt:message key="aimir.approach"/>)</span>
					</li>
			  	</ul>
	        </div>
         
	        <div class="gaige_graph ptrbl10">
	            <div id="guageChartDiv">
	                The chart will appear within this DIV. This text will be replaced by the chart.
	            </div>
			</div> 
        
	        <div class="gaigeValue">
	        	<span><fmt:message key="aimir.contract.demand.amount"/></span>
	        	<span>
	        		<input id="contractEnergy" type="text" value="" maxlength="2" 
	        			class="input_dot" readonly style="width:60px; margin-top: -5px;"/>
	        	</span>
	        	<span>kW</span>	           
	        	<span><fmt:message key="aimir.facilityMgmt.powerConsumption"/></span>
	        	<span>
	           		<input id="useAmount" type="text" value="" maxlength="2" 
	           			class="input_dot" readonly style="width:50px; margin-top: -5px;"/>
	           	</span>
	        	<span>kW</span>
	       </div>    
		</div>
	    <!-- 계약전력과 Peak Demand gauge chart (E) -->    

	    <!-- 계약전력과 Peak Demand multiple axes chart (S) -->
	    <div id="bldg_peak_graph">
	        <div id="columnChartDiv" align="left">
	            The chart will appear within this DIV. This text will be replaced by the chart.
	        </div>
	    </div> 
	   <!-- 계약전력과 Peak Demand multiple axes chart (E) -->
	</div>
	</div>

	<!-- include javascripts -->
	<script type="text/javascript" src="${ctx}/js/framework/Config/bems/fmtMessage.do"></script>
	<script type="text/javascript">
		GLOBAL_CONTEXT.GADGET = "ContractEnergyPeakDemand";
		GLOBAL_CONTEXT.SIZE = "MIN";
	</script>	
	<script data-main="${ctx}/js/bems_main" src="${ctx}/js/require-jquery.js" type="text/javascript"></script>
</body>
</html>