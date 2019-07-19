<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>

<th><fmt:message key="aimir.modemid" /></th>
<td class="padding-r20px"><input type="text" id="modemId"
	name="deviceSerial" /> <input type="hidden" id="modemIdHidden"
	value="<c:catch>${modem.deviceSerial}</c:catch>" /></td>

<th><fmt:message key="aimir.modem.type" /></th>
<td class="padding-r20px"><select id="modemType"></select> <input
	type="hidden" id="modemTypeHidden"
	value="<c:catch>${modem.modemType}</c:catch>" /></td>

<th><fmt:message key="aimir.mcuid" /></th>
<td><input type="text" id="mcuId" name="mcu.sysID" /> <input
	type="hidden" id="mcuIdHidden"
	value="<c:catch>${modem.mcu.sysID}</c:catch>" /></td>

<th><fmt:message key="aimir.fw.hwversion" /></th>
<td class="padding-r20px"><input type="text" id="hwVersion" value="<c:catch>${modem.hwVer}</c:catch>" />
	<input type="hidden" id="hwVersionHidden" value="<c:catch>${modem.hwVer}</c:catch>" /></td>

<th><fmt:message key="aimir.mcucode.fmversion" /></th>
<td class="padding-r20px"><input type="text" id="fwVersion" name="fwVer" value="<c:catch>${modem.fwVer}</c:catch>" />
	<input type="hidden" id="fwVersionHidden" value="<c:catch>${modem.fwVer}</c:catch>" /></td>

<th><fmt:message key="aimir.mcucode.fmbuild" /></th>
<td class="padding-r20px"><input type="text" id="fwRevision"
	name="fwRevision" value="<c:catch>${modem.fwRevision}</c:catch>" /> <input
	type="hidden" id="fwRevisionHidden"
	value="<c:catch>${modem.fwRevision}</c:catch>" /></td>

<th><fmt:message key="aimir.protocol" /></th>
<td><select id="protocolType"></select> <input type="hidden"
	id="protocolTypeHidden"
	value="<c:catch>${modem.protocolType}</c:catch>" /></td>
		
<th><fmt:message key="aimir.vendor" /></th>
<td class="padding-r20px"><select id="deviceVendor"
	name="model.deviceVendor.id"
	onChange="javascript:getModelListByVendor(0);"></select> <input
	type="hidden" id="deviceVendorHidden"
	value="<c:catch>${modem.model.deviceVendor.id}</c:catch>" /></td>

<th><fmt:message key="aimir.model" /></th>
<td class="padding-r20px"><select id="modelId" name="model.id"></select>
	<input type="hidden" id="modelIdHidden"
	value="<c:catch>${modem.model.id}</c:catch>" /></td>

<th><fmt:message key="aimir.location" /></th>
<td><input name="searchWord_1" id='searchWord_1' type="text"
	style="width: 90px" value="${modem.location.name}" /> <input
	type='hidden' id='infolocationId' name="location.id" value=''></input>
</td>

<th><fmt:message key="aimir.installationdate" /></th>
<td><input type="text" id="installDate" name="installDate" /> <input
	type="hidden" id="installDateHidden"
	value="<c:catch>${modem.installDate}</c:catch>" /></td>

<th><fmt:message key="aimir.txpower" /></th>
<td><input type="text" id="rfPower" value="<c:catch>${modem.rfPower}</c:catch>" name="rfPower" /> <input
	type="hidden" id="rfPowerHidden"
	 /></td>

<th><fmt:message key="aimir.node.kind" /></th>
<td class="padding-r20px"><input type="text" id="nodeKind"
	name="nodeKind" /> <input type="hidden" id="nodeKindHidden"
	value="<c:catch>${modem.nodeKind}</c:catch>" /></td>

<th><fmt:message key="aimir.lastlinktime" /></th>
<td class="padding-r20px"><input type="text" class="border-trans"
	readonly="readonly" value="${lastLinkTime}" /></td>

<th><fmt:message key="aimir.status" /></th>
<td><select id="modemStatusEdit"></select> <input type="hidden"
	id="modemStatusCodeId" name="modemStatusCodeId"
	value="<c:catch>${modem.modemStatus.id}</c:catch>" /></td>