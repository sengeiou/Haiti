<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<script type="text/javascript">
    $(document).ready(function() {
        // 권한체크
        if (editAuth == "true") {
            $("#supplytypeAddBtn").show();
        } else {
            $("#supplytypeAddBtn").hide();
        }
    });
</script>
<form:form id="supplyTypeAddForm" modelAttribute="supplyType">

<table>
<tr><td>

	<table class="addsupplytype wfree">
		<tr>
			<td class="addsupplytype-select">
				<select name="typeCode.id">
					<c:forEach var="type" items="${codeList}">
						<option value="${type.id}">
							${type.descr}
						</option>
					</c:forEach>
				</select>
			</td>
			<td class="space20"></td>
			<td class="gray11pt withinput"><fmt:message key='aimir.co2formula2'/> <fmt:message key='aimir.name'/>: </td>
			<td class="addsupplytype-input-co2name"><input type="text" name="co2Formula.name" onkeypress="CheckKeyPress();" /></td>
			<td class="space20"></td>
			<td class="gray11pt withinput"><fmt:message key='aimir.paydate'/> : </td>
			<td class="addsupplytype-input-date"><input type="text" name="billDate" id="billDate" onkeypress="CheckKeyPress();" /></td>
			<td class="space20"></td>
		</tr>
    </table>

	</td>
	<td>
        <div id="supplytypeAddBtn">
    		<div id="gadget_btn" class="addsupplytype">
    			<ul><li id="gadget_plus"><a href="javascript:submitType('add')" title='<fmt:message key='aimir.add'/>'></a></li></ul>
    		</div>
        </div>
	</td></tr>
</table>


<div class="dashedline-supply"><ul><li></li></ul></div>
</form:form>

