<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<script type="text/javascript">
    $(document).ready(function() {
        // 권한체크
        if (editAuth == "true") {
            $("#locationAddBtn").show();
        } else {
            $("#locationAddBtn").hide();
        }
    });
</script>
<form:form id="locationServiceAddForm" modelAttribute="supplyTypeLocation">

<table>
<tr><td>

	<table class="addsupplytype wfree">
		<tr>
			<td class="addsupplytype-select">
				<select name="supplyType.id">
					<c:forEach var="supplyType" items="${supplyTypeList}">
						<option value="${supplyType.id}">
							${supplyType.typeCode.name}
						</option>
					</c:forEach>
				</select>
				<input type="hidden" id="supplyTypeLocation" name="location.id"/>
			</td>
			<td class="gray11pt withinput"><fmt:message key="aimir.constract.capacity"/> : </td>
			<td class="addsupplytype-input-capacity"><input type="text" name="contractCapacity.capacity"/></td>
		</tr>
    </table>

	</td>
	<td>
		<div id="locationAddBtn">
            <div id="gadget_btn" class="addsupplytype">
    			<ul><li id="gadget_plus"><a href="javascript:submitLocationService('add')" title='<fmt:message key='aimir.add'/>'></a></li></ul>
    		</div>
        </div>
	</td></tr>
</table>


	<div class="dashedline-supply"><ul><li></li></ul></div>
</form:form>
