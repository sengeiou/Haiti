<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<form:form id="eventAlertSearchForm" modelAttribute="eventAlert">
	<ul>
		<li style="width:8%"><fmt:message key='aimir.severity'/></li>
		<li style="width:20%;padding:3px 0 0 5px">
			<select name="severity" class="nuri_search" id="select2" style="padding:4px; width:120px">
				<c:forEach var="type" items="${severityList}">
					<option value="${type.id}">
						${type.name}
					</option>
				</c:forEach>
			</select>
			<select name="select2" class="nuri_search" id="select2" style="padding:4px; width:60px">
				<option>=</option>
				<option><=</option>
				<option selected="selected">>=</option>
			</select>
		</li>  
		<li style="width:8%"><fmt:message key='aimir.state'/></li>
		<li style="width:20%;padding:3px 0 0 5px">
			<select name="status" class="nuri_search" id="select2" style="padding:4px; width:120px">
				<c:forEach var="type" items="${statusList}">
					<option value="${type.id}">
						${type.name}
					</option>
				</c:forEach>
			</select>
		</li>
		<li style="width:8%"><fmt:message key='aimir.location'/></li>
		<li style="width:20%;padding:3px 0 0 5px">
			<select name="select2" class="nuri_search" id="select2" style="padding:4px; width:120px">
			</select>
		</li>
	</ul>
	<ul>
		<li style="width:8%;"><fmt:message key='aimir.equiptype'/></li>
		<li style="width:20%;padding:3px 0 0 5px">
			<select name="select2" class="nuri_search" id="select2" style="padding:4px; width:120px">
				<c:forEach var="type" items="${systemList}">
					<option value="${type.id}">
						${type.name}
					</option>
				</c:forEach>
			</select>
		</li>
		<li style="width:8%"><fmt:message key='aimir.equipid'/></li>
		<li style="width:20%;padding:3px 0 0 5px">
			<input type="text" class="nuri_search_n" name="deviceId" style="width:150px;"/>
		</li>
		<li style="width:8%"><fmt:message key='aimir.alert'/>/<fmt:message key='aimir.event'/></li>
		<li style="width:20%;padding:3px 0 0 5px">
			<select name="select2" class="nuri_search" id="select2" style="padding:4px; width:120px">
				<option selected="selected"><fmt:message key='aimir.alert'/>&<fmt:message key='aimir.event'/></option>
				<option><fmt:message key='aimir.alert'/></option>
				<option><fmt:message key='aimir.event'/></option>
			</select>
		</li>
	</ul>
		<!--
		<li style="padding:3px 0 0 5px">
			<select name="select2" class="nuri_search" id="select2" style="padding:4px; width:120px">
			</select>
		</li>
		-->
		<!--
		<li style="padding:3px 0 0 5px">
			<select name="select2" class="nuri_search" id="select2" style="padding:4px; width:120px">
			</select>
		</li>
		-->
	<ul>
		<li style="float:right; padding:0 10px 0 0">
			<div id="nuri_btn" style="float:right"><ul>
				<li class="arrdn"><a href="#"><span ><fmt:message key='aimir.button.apply'/></span></a></li></ul>
			</div>
		</li>
	</ul>
</form:form>
