<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>


<form:form id="supplierDefault" modelAttribute="supplier">
    <div style="margin: 10px">
    <ul>
    <li class="supplier-table">
	    <table width = "400px" border="1" bordercolor="#b4d3f0">
			<tr><td id="supplier-li1" class="red"><fmt:message key="aimir.supplier.name"/></td>
			<td id="supplier-li3"><input type="text" name="name"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.description"/></td>
			<td id="supplier-li3"><input type="text" name="descr"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.tel.no"/></td>
			<td id="supplier-li3"><input type="text" name="telno"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.country"/></td>
			<td id="supplier-li3">
				<select name="country.id">
	                <c:forEach var="country" items="${countryList}">
	                    <option value="${country.id}">
	                        ${country.name}
	                    </option>
	                </c:forEach>
	            </select></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.supplier.address"/></td>
			<td id="supplier-li3"><input type="text" name="address"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.language"/></td>
			<td id="supplier-li3">
				<select name="lang.id">
	                <c:forEach var="lang" items="${languageList}">
	                    <option value="${lang.id}">
	                    	${lang.name}
	                    </option>
	                </c:forEach>
	            </select>
			</td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.timezone"/></td>
			<td id="supplier-li3">
				<select name="timezone.id">
	                <c:forEach var="timezone" items="${timeZoneList}">
	                    <option value="${timezone.id}">
	                        ${timezone.name}
	                    </option>
	                </c:forEach>
	            </select>
			</td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.md.pattern"/></td>
			<td id="supplier-li3"><input type="text" name="md.pattern"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.cd.pattern"/></td>
			<td id="supplier-li3"><input type="text" name="cd.pattern"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.md.groupingSeperator"/></td>
			<td id="supplier-li3"><input type="text" name="md.groupingSeperator"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.cd.groupingSeperator"/></td>
			<td id="supplier-li3"><input type="text" name="cd.groupingSeperator"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.md.decimalSeperator"/></td>
			<td id="supplier-li3"><input type="text" name="md.decimalSeperator"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.cd.decimalSeperator"/></td>
			<td id="supplier-li3"><input type="text" name="cd.decimalSeperator"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.md.round"/></td>
			<td id="supplier-li3"><input type="text" name="md.round"/></td></tr>
			<tr><td id="supplier-li1"><fmt:message key="aimir.cd.round"/></td>
			<td id="supplier-li3"><input type="text" name="cd.round"/></td></tr>
	    </table>
    </li>

    </ul>
    </div>

</form:form>
