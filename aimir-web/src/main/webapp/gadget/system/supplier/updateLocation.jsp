<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<form id="locationUpdateForm" modelAttribute="location">
	<div class="headspace-enter"><span class="openfolder"><input type="text" readonly name="hearderName" value="${location.name}"/></span></div>

	<div class="clear"></div>
	<div class="supplierlocation-detail">
		<table class="customer_detail_noborder">
		<tr><th class="darkgraybold11pt"><fmt:message key="aimir.parent.location"/></th>
			<td><input type="text" readonly name="parentName" value="${location.parent.name}" class="noborder"/></td></tr>
		<tr><th class="darkgraybold11pt"><fmt:message key="aimir.siteName"/></th>
			<td><input type="text" name="name" id="locationName" value="${location.name}"/></td></tr>
		<tr><th class="darkgraybold11pt"><fmt:message key="aimir.orderNo"/></th>
			<td><input type="text" id="orderNo" name="orderNo" value="${location.orderNo}"/></td></tr>
		</table>
	</div>

	<div class="clear"></div>
	<div class="btn_right_bottom">
		<span id="btn"><ul><li style="margin:0;"><a href="javascript:submitLocation('update')" class="on"><fmt:message key="aimir.ok"/></a></li></ul></span>
		<span id="btn"><ul><li style="margin:0;"><a href="javascript:getLocation()"><fmt:message key="aimir.cancel"/></a></li></ul></span>
	</div>

</form>

