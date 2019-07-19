<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<form id="locationAddForm" modelAttribute="location">

	<div style="height:12px;"></div>
	<div class="headspace-enter">
		<label class="check"><fmt:message key="aimir.add"/></label>
	</div>

	<div class='clear'></div>
	<div class='supplierlocation-detail'>
		<table class="customer_detail_noborder">
		<tr><th class="darkgraybold11pt withinput"><fmt:message key="aimir.parent.location"/></th>
			<td>${location.parent.name}<input type="hidden" id="parentId" name="parent.id" value="${location.parent.id}"/></td></tr>
		<tr><th class="darkgraybold11pt withinput"><fmt:message key="aimir.siteName"/></th>
			<td><input type="text" id="locationName" name="name" onkeypress="CheckKeyPress();"/></td></tr>
		<tr><th style="padding:0 !important;">
				<!-- <input type="checkbox" name="checkTop" class="trans" style="width:18px !important;" /> -->
				<span class="darkgraybold11pt withinput"><fmt:message key="aimir.orderNo"/></span></th>
			<td><input type="text" id="orderNo" name="orderNo" onkeypress="CheckKeyPress();"/></td></tr>
        <tr><th style="padding:0 !important;">
                <span class="darkgraybold11pt withinput"><fmt:message key="aimir.rootlevel"/></span></th>
            <td><input type="checkbox" id="checkTop" name="checkTop" class="trans" style="width:18px !important;"/></td></tr>
		</table>
	</div>

	<div class="btn_right_bottom">
		<span id="btn">
			<ul><li style="margin:0;"><a href="javascript:submitLocation('add')" class="on"><fmt:message key='aimir.add'/></a></li></ul>
		</span>
		<span id="btn">
			<ul><li style="margin:0;"><a href="javascript:bindingLocation()"><fmt:message key='aimir.cancel'/></a></li></ul>
		</span>
	</div>

    <div class='supplierlocation-detail'>
        <table class="customer_detail_noborder">
        <tr><td style="padding:0 !important;">
                <font style="color:red;">(<fmt:message key="aimir.msg.addlocation.additionaldesc"/>)</font></td>
        </tr>
        </table>
    </div>

</form>