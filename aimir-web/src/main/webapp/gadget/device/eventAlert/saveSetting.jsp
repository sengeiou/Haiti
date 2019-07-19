<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<fmt:message key='aimir.popup.setting'/>
<fmt:message key='aimir.popup.window'/>
<fmt:message key='aimir.popup.window.count'/>
<fmt:message key='aimir.sound'/>
<fmt:message key='aimir.sound.select'/>
<fmt:message key='aimir.sound.setting'/>
<fmt:message key='aimir.max.count'/>
<fmt:message key='aimir.max'/>
<fmt:message key='aimir.time.closedalertdata'/>
<fmt:message key='aimir.sec'/>
<fmt:message key='aimir.yes'/>
<fmt:message key='aimir.no'/>
<form:form id="supplierType" modelAttribute="supplyType">
	
	<ul>
		<li style="width:150px;padding:0 0 10px 0">
			<input type="checkbox" name="c1" checked="checked">
		</li>
		<li style="width:80px"><fmt:message key='aimir.paydate'/></li>
		<li style="width:120px">
			<input type="text" class="nuri_search_n" name="billDate" style="width:70px; height:15px;"/>
		</li>
		<li><fmt:message key='aimir.co2formula'/></li>
		<li>
			<input type="text" class="nuri_search_n" name="co2Formula" style="width:70px; height:15px;"/>
		</li>
		<li>㎏ CO₂ </li> 
	</ul>
	<br/><br/>
	<div id="nuri_btn" align="left" style="clear:both;">
		<table>
			<tr>
				<td>
					<li class="arrdn">
						<a href="javascript:submitType('add')"><span><fmt:message key="aimir.add"/></span></a>
					</li>
				</td>
				<td>
					<li class="arrdn">
						<a href="javascript:getSupplier()"><span><fmt:message key="aimir.cancel"/></span></a>
					</li>
				</td>
			</tr>
		</table>
	</div>
</form:form>
