<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

    <script>
	function updateContractCapacity() {
	// 변경한 값을 Form에 설정 해 준다.
	//alert($("#constractCapacity").val());
	$("#locationServiceUpdateForm :input[name='contractCapacity.capacity']").val($("#constractCapacity").val());
	
	
	// 과금일 정보 갱신 JS 호출
	submitLocationService('update'); 
}
</script>	
<form:form id="locationServiceUpdateForm" name="locationServiceUpdateForm" modelAttribute="supplyTypeLocation">

<table>
	<tr><td style="padding:0 !important;">

		<table class="addsupplytype-table">
			<tr>
				<td class='addsupplytype-select bluebold11pt withinput'>${supplyTypeLocation.supplyType.typeCode.name}</td>
				<td class='addsupplytype-label-amount'><fmt:message key="aimir.constract.capacity"/> : </td>
				<td class="addsupplytype-input-capacity"><input type="text" id="constractCapacity" name="contractCapacity.capacity" value="${supplyTypeLocation.contractCapacity.capacity}"/></td>
			</tr>	


			</table>
		</td>
		<td>
			<div class="btn btn-updatesupply-confirm">
					<ul><li><a href="javascript:updateContractCapacity()" class="on"><fmt:message key="aimir.ok"/></a></li></ul>
					<ul><li><a href="javascript:getLocation()"><fmt:message key="aimir.cancel"/></a></li></ul>
			</div>
		</td>
	</tr>
</table>

</form:form>