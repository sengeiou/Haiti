<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="PRAGMA" content="NO-CACHE">
    <meta http-equiv="Expires" content="-1">
<style type="text/css">
html, body{height:100%;}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>EMS Report</title>
<link href="${ctx}/css/style_bems.css" rel="stylesheet" type="text/css">

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" >
var message = "하하하하";

$(document).ready(function(){
    var localport = "<%= request.getLocalPort() %>";
    var birtURL = opener.window.birtURL;
    document.getElementById('report_frame').src=birtURL + "&localPort=" + localport;
});
</script>
</head>
<body>
<iframe id="report_frame"  width="100%" height="100%">
</iframe>
</body>
</html>