<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
	<link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
	<script type="text/javascript" charset="utf-8">
		function init() {
			$.getJSON('${ctx}/gadget/system/location/getLocations.do',
				function(json) {
					var target;
					
					$.each(json.locations, function(index, location) {
						target = document.getElementById("location");
						target.options.add(new Option(location['name'], location['id']), index);
					});
				});
		}
	</script>
</head>
<body onLoad="init();">

<!-- 메인 시작 -->
<div style="width:100%; float:left">
	<ul>
		<li style="width:25%">
			<fmt:message key='aimir.location'/>
			<select id="location" name="location" class="nuri_search" style="padding:4px; width:120px"></select>
		</li>
	</ul>
</div>
<!-- 메인 끝 -->
</body>
</html>
