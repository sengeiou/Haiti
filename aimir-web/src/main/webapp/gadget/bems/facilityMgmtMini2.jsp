<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!--[if lt IE 9]>
	<script src="${ctx}/js/html5shiv.js"></script>
	<![endif]-->
	<title></title>
	<%@ include file="/gadget/system/preLoading.jsp"%>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bems/imports_style.css"/>
	<link rel="stylesheet" type="text/css" href="${ctx}/css/bems/facilityMgmt.css"/>
	
</head>
<body>

	<div id="wrapper" class="min">
		<div>
			<div id="location_tree">		
			</div>
			<div id="end_device_pie_chart">
			</div>	
			<div id="end_device_chart"></div>
		</div>
		
	</div>
	
	<script type="text/javascript" src="${ctx}/js/framework/Config/bems/fmtMessage.do"></script>
	<script type="text/javascript">
		GLOBAL_CONTEXT.GADGET = "FacilityMgmt";
		GLOBAL_CONTEXT.SIZE = "MIN";
	</script>	
	<script data-main="${ctx}/js/bems_main" src="${ctx}/js/require-jquery.js" type="text/javascript"></script>
</body>
</html>