<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGRA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>POC Test</title>

<link href="${ctx}/css/style_firmware.css" rel="stylesheet" type="text/css">    

<script type="text/javascript" charset="utf-8" src="${ctx}/js/public.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.cookie.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.hotkeys.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.metadata.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/sarissa.js"></script>
<script type="text/javascript" src="${ctx}/js/tree/jquery.tree.js"></script>
<script type="text/javascript" src="${ctx}/js/firmware/plugins/jquery.tree.checkbox.js"></script>
<script type="text/javascript" src="${ctx}/js/firmware/plugins/jquery.tree.xml_flat.js"></script>
<script type="text/javascript" src="${ctx}/js/firmware/plugins/jquery.tree.xml_nested.js"></script>
<script type="text/javascript" src="${ctx}/js/firmware/autocomplete/jquery.autocomplete.js"></script>
</head>

 <script type="text/javascript" charset="utf-8">

    	var supplierId = "";
    	var loginId = "";
    	var top_equip_type = "";
    	var change_check= "";

	    /**
	     * 유저 세션 정보 가져오기
	     */
	    $.getJSON('${ctx}/common/getUserInfo.do',
	            function(json) {
	                if(json.supplierId != ""){
	                    supplierId = json.supplierId;
	                    loginId = json.loginId;
	                }
	            }
	    );

	   
</script>

<body>
  <br><br>
<table align="center"><tr><td align="left">
<br>
<p><b>Please, maximize this gadget for further information.</b></p>
</td></tr></table>
</body>
</html>