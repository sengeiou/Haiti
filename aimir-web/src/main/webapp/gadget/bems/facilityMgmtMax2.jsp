<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 	<!--[if lt IE 9]>
 	<script src="${ctx}/js/html5shiv.js"></script>
 	<![endif]-->
	<%@ include file="/gadget/system/preLoading.jsp"%>
	<title></title>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bems/imports_style.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bems/facilityMgmt.css"/>
</head>
<body>

	<div id="wrapper" class="max">
		<div>		
			<div id="location_tree">		
			</div>
			<div class="chart_and_grid">
				<div id="end_device_chart"></div>
				
				<div class="excel_button">
						<em class="hm_button">
							<a class="excel"><fmt:message key="aimir.button.excel"/></a>
						</em>
				</div>					

				<div id="facility_data">	
				
					<div id="situation_grid"></div>			
					<div id="status_grid"></div>			
					<div id="history_grid"></div>
				</div>			
				<div id="end_device_situation_form"></div>
				<div id="end_device_status_form"></div>
				<div id="stat_chart_grid">
					<div id="dailyChart" class="used_chart"></div>
					<div id="weeklyChart" class="used_chart"></div>
					<div id="monthlyChart" class="used_chart"></div>
					<div id="yearlyChart" class="used_chart"></div>
					<div class="g_clear"></div>
				</div>
			</div>
			<div class="g_clear"></div>
		</div>
	</div>
	
	
	<script type="text/javascript" src="${ctx}/js/framework/Config/bems/fmtMessage.do"></script>
	<script type="text/javascript">
		GLOBAL_CONTEXT.GADGET = "FacilityMgmt";
		GLOBAL_CONTEXT.SIZE = "MAX";
	</script>	
	<script data-main="${ctx}/js/bems_main" src="${ctx}/js/require-jquery.js" type="text/javascript"></script>
</body>
</html>