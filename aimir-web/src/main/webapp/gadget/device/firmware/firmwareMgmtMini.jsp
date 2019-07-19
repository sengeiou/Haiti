<%
/**
 * Copyright Nuri Telecom Corp.
 * 파일명: firmWareMainGadget.jsp
 * 작성일자/작성자 : 2010.12.06 최창희
 * @see 
 *
 * 펌웨어 관리자 페이지 Component
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * ============================================================================
 */
 %>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGRA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">
<title>FirmwareMgmtGadget</title>

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
<!--
    이 가젯은 Concentrator, Modem의 펌웨어 파일들을 버전별로 관리하며
       장비들에게 일괄 배포 합니다.추 후 배포 결과 및 진행 상태 확인을 할 수 있습니다.
    이 기능을 이용하기 위해서는 Maximize this gadget please!
  -->
  <br><br>
<table align="center"><tr><td align="left">
<p><b>The user can manage the firmware of the concentrator and modem by version and distribute the firmware to the equipment bloc.</b></p>
<p><b>After distributing the firmware, the user can check the result and the status.</b></p>
<br>
<p><b>Please, maximize this gadget for further information.</b></p>
</td></tr></table>
</body>
</html>