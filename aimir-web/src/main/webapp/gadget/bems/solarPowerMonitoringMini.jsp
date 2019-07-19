<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" 
	contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!--[if lt IE 9]>
	<script src="${ctx}/js/html5shiv.js"></script>
	<![endif]-->
    <title><fmt:message key="gadget.bems.solarPowerMonitoring"/></title>
    <link href="${ctx}/css/index.css" rel="stylesheet" type="text/css" />
    <link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css" />
 	<link href="${ctx}/css/bems/imports_style.css" rel="stylesheet" type="text/css" />

 	<link href="${ctx}/css/bems/solarPowerMonitoring.css" rel="stylesheet" type="text/css" />
    <%@ include file="/gadget/system/preLoading.jsp"%>
</head>
<body>
<div id="wrapper" class="min">
	<div class="top-wrapper">
		<div id="weather">
			<div class="current-weather"></div>
			<span></span>
		</div>
		<div id="solar-power-station">
			<img src="${ctx}/images/weather/generation_plate.jpg" 
				alt="solar power generation plate" title="solar power generation plate" />
		</div>
		<div class="g_clear"></div>
	</div>	

	<div class="dashedline"></div>

	<div class="bottom-wrapper">
		<div id="current-information">
			<ul>
				<li class="chart-title">
					<span class="hilght-label">
						<fmt:message key="aimir.bems.solar.electricGeneration"/>
					</span>
					<span id="current-electric" class="mr5"></span>
					<fmt:message key="aimir.unit.kwh"/>					
				</li>
				<li class="chart-title">
					<span class="hilght-label">
						<fmt:message key="aimir.bems.solar.electricGenAccumulatedToday"/>
					</span>
					<span id="accumulated-electric" class="mr5"></span>					
					<fmt:message key="aimir.unit.kwh"/>
				</li>
			</ul>
		</div>
		<div id="electric-generation-chart"></div>	
	</div>
</div>

<!-- include javascripts -->
<script type="text/javascript" src="${ctx}/js/framework/Config/bems/fmtMessage.do"></script>
<script type="text/javascript">
	GLOBAL_CONTEXT.GADGET = "SolarPowerMonitoring";
	GLOBAL_CONTEXT.SIZE = "MIN";
</script>	
<script data-main="${ctx}/js/bems_main" src="${ctx}/js/require-jquery.js" type="text/javascript"></script>
</body>
</html>