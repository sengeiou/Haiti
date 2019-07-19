<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>
<script>
function updateSupplierBillDate() {
	// 변경한 값을 Form에 설정 해 준다.
	//$("#supplyTypeUpdateForm :input[name='billDate']").val($("#billDate").val());
	// 과금일 정보 갱신 JS 호출
	submitType('update');
}
</script>
<form:form id="supplyTypeUpdateForm" name="supplyTypeUpdateForm" modelAttribute="supplyType">

<table>
	<tr><td style="padding:0 !important;">

			<table class="wfree">
				<tr>
					<td class="addsupplytype-select bluebold11pt withinput">${supplyType.typeCode.name}</td>
					<td class="gray11pt withinput"><input type="text" name="co2Formula.name" id="co2Formula.name" value="${supplyType.co2Formula.name}"/></td>
					<td class="gray11pt withinput"><fmt:message key='aimir.paydate'/> : </td>
					<td class="addsupplytype-input-date"><input type="text" name="billDate" id="billDate" value="${supplyType.billDate}"/></td>

					<td class="gray11pt withinput"><fmt:message key='aimir.co2formula'/> : </td>
					<td class="addsupplytype-input-co2"><input type="text" name="co2Formula.co2emissions" value="${supplyType.co2Formula.co2emissions}"/></td>
					<td class="lightgray11pt withinput">kg CO₂</td>

				</tr>
			</table>
		</td>

		<td>
			<div class="btn btn-updatesupply-confirm">
				<ul><li><a href="javascript:updateSupplierBillDate()" class="on"><fmt:message key="aimir.ok"/></a></li></ul>
				<ul><li><a href="javascript:getSupplier()"><fmt:message key="aimir.cancel"/></a></li></ul>
			</div>
		</td>
	</tr>
</table>

</form:form>



