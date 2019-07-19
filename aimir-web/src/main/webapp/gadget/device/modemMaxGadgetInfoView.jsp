<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>

<th><fmt:message key="aimir.modemid" /></th>
<td class="padding-r20px"><input type="text" id="modemIdView"
	class="border-trans" readonly="readonly" /></td>

<th><fmt:message key="aimir.modem.type" /></th>
<td class="padding-r20px"><input type="text" id="modemTypeView"
	class="border-trans" readonly="readonly" /></td>

<th><fmt:message key="aimir.mcuid" /></th>
<td><input type="text" id="mcuIdView" class="border-trans "
	readonly="readonly" value="<c:catch>${modem.mcu.sysID}</c:catch>" /></td>

<th><fmt:message key="aimir.fw.hwversion" /></th>
<td class="padding-r20px"><input type="text" id="hwVersionView"
	class="border-trans" readonly="readonly" /></td>

<th><fmt:message key="aimir.mcucode.fmversion" /></th>
<td class="padding-r20px"><input type="text" id="fwVersionView"
	class="border-trans " readonly="readonly"
	value="<c:catch>${modem.fwVer}</c:catch>" /></td>

<th><fmt:message key="aimir.mcucode.fmbuild" /></th>
<td class="padding-r20px"><input type="text" id="fwBuildView"
	class="border-trans" readonly="readonly"
	value="<c:catch>${modem.fwRevision}</c:catch>" /></td>

<th><fmt:message key="aimir.protocol" /></th>
<td><input type="text" id="protocolTypeView" class="border-trans "
	readonly="readonly" /></td>
	
<th><fmt:message key="aimir.vendor" /></th>
<td class="padding-r20px"><input type="text" id="deviceVendorView"
	class="border-trans" readonly="readonly" /></td>

<th><fmt:message key="aimir.model" /></th>
<td class="padding-r20px"><input type="text" id="modelIdView"
	class="border-trans" readonly="readonly" /></td>

<th><fmt:message key="aimir.location" /></th>
<td><input type="text" id="locationView" class="border-trans "
	readonly="readonly" /></td>

<th><fmt:message key="aimir.installationdate" /></th>
<td><input type="text" class="border-trans" readonly="readonly"
	value="${installDate}" /></td>

<th><fmt:message key="aimir.txpower" /></th>
<td><input type="text" id="txPowerView" class="border-trans"
	readonly="readonly" value = "${modem.rfPower}"/></td>

<th><fmt:message key="aimir.node.kind" /></th>
<td class="padding-r20px"><input type="text" id="nodeKindView"
	class="border-trans" readonly="readonly" /></td>

<th><fmt:message key="aimir.lastlinktime" /></th>
<td class="padding-r20px"><input type="text" class="border-trans"
	readonly="readonly" value="${lastLinkTime}" /></td>

<th><fmt:message key="aimir.status" /></th>
<td class="padding-r20px"><input type="text" class="border-trans"
	id='modemStatusView' readonly="readonly"
	value="${modem.modemStatus.descr}" /></td>