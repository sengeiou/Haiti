<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="PRAGMA" content="NO-CACHE">
<meta http-equiv="Expires" content="-1">

<title>Account</title>

<link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
<link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
<link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
<script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
<script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
<script type="text/javascript" src="http://developmentor.lrlab.to/postal/js/postalua.js"></script>
<script type="text/javascript">

	function searchZipCode(search) {

		var coder = new Postal.Coder(); 
		//var coder = new Postal.Coder({timeout: 60}); 검색타임아웃시간을 연장할 경우.
		
		coder.onload = function() {

			var resultDiv = document.getElementById("searchResult");

			resultDiv.innerHTML = "";

	     	// 우편번호검색 결과가 존재하지 않을때
		 	if (coder.response.length == 0) {
			 	
	       		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"우편번호검색 결과가 존재하지 않습니다. 검색할 주소를 입력해 주세요.");
		 	}

	    	for (var i=0; i<coder.response.length; i++) {
		    	
	      		var addr = coder.response[i];
	      		var resultP = document.createElement("p");
	      		
	      		resultP.appendChild(document.createTextNode(addr.search));
	      		resultDiv.appendChild(resultP);
	    	}
	  	};

	   	// 검색할 주소 체크
	  	if (!coder.send(search)) {
		  	
	  		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"검색할 주소를 입력해 주세요.");

	  		return;
	  	}

	  	// 에러가 발생했할 경우
	 	coder.onerror = function() {

	  		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"우편번호 검색에서 에러가 발생했습니다. 관리자에게 문의 하십시요.");
	  		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"에러코드 : " + coder.status);
	 		Ext.Msg.alert('<fmt:message key='aimir.message'/>',"에러메시지 : " + coder.statusText);
		};

		coder.send(search);
	}

</script>
</head>
<body>
<div id="wrapper">
	<div class="popup_title">우편번호 검색</div>
	<!--search area -->
	<div class="align_center">
		<div class="search_zipcode">
		<span class="text_side5">주소검색</span>
		<span><input name="search" id="search" type="text"/></span>
		<span class="hm_button"><a href="javascript:searchZipCode(document.getElementById('search').value);">Search</a></span>
		</div>
		<p class="clear padding_t10">찾고자 하는 주소명을 검색해 주십시오.</p>
		<p class="text_gray7">(예) 가산동, 구로동, 대림동</p>
	</div>
	<!--//search area -->
	<!--result search -->
	<div style="display:block">
		<p class="padding_t20 borderbottom_gray"></p>
		<div class="margin_10">
			<p>총 <font class="text_orange"><b>39개</b></font>의 우편번호가 검색되었습니다.</p>
			<p class="text_gray7">주소를 선택하면 자동으로 입력됩니다.</p>
			<!--result data  -->
			<div class="zipcode_box margin_t5">
				<div id="searchResult" class="margin_10">
				</div>
			</div>
			<!--//result data  -->
		</div>
	</div>
	<!--//result search -->
</div>
</body>
</html>