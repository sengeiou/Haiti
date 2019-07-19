<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

<script type="text/javascript" charset="utf-8">
function checkProtocol(){
	var selectedValue = $('#protocolType option:selected').text();
	var modemIpTag_1 = "";
	var modemIpTag_2 = "";
	var modemIpTag_3 = "";
	var modemIpTag_4 = "";
	
	if(selectedValue == "IP" || selectedValue == "SMS" || selectedValue == "GPRS"){
		modemIpTag_1 = "IPv4 Address"; 
		modemIpTag_2 = "<input type='text' id='ipAddr' name='ipAddr' />";
		modemIpTag_3 = "IPv6 Address"; 
		modemIpTag_4 = "<input type='text' id='ipv6Address' name='ipv6Address' />";
	}
		$('#modemIpSection_1').html(modemIpTag_1);
		$('#modemIpSection_2').html(modemIpTag_2);
		$('#modemIpSection_3').html(modemIpTag_3);
		$('#modemIpSection_4').html(modemIpTag_4);
}
</script>

<!-- 수정Div -->
    <div id="modemInfoEdit" style="display:block;">
	   <form id="modemInfoForm">
	            <table class="wfree">
	            <tr>
	            	<th><fmt:message key="aimir.modemid"/></th>
	                <td class="padding-r20px"><input type="text" id="modemId" name="deviceSerial" /></td>
					<th><fmt:message key="aimir.modem.type"/></th>
	                <td class="padding-r20px"><select id="modemType"></select></td>
	                <th><fmt:message key="aimir.mcuid"/></th>
	                <td><input type="text" id="mcuId" name="mcu.sysID" /></td>
	            </tr>
	            <tr><th><fmt:message key="aimir.fw.hwversion"/></th>	
	                <td><input type="text" id="hwVersion" name="hwVersion" /></td>
	                <th><fmt:message key="aimir.mcucode.fmversion"/></th>
	                <td><input type="text" id="fwVersion" name="fwVersion" /></td>
	                <th><fmt:message key="aimir.protocol"/></th>
	                <td><select id="protocolType" onChange="javascript:checkProtocol();"></select></td>
	            </tr>
	            <tr><th><fmt:message key="aimir.vendor"/></th>
	                <td class="padding-r20px"><select id="deviceVendor" name="model.deviceVendor.id" onChange="javascript:getModelListByVendor(0);"></select></td>
	                <th><fmt:message key="aimir.model"/></th>
	                <td class="padding-r20px"><select id="modelId" name="model.id"></select></td>
	                <th><fmt:message key="aimir.installationdate"/></th>
	                <td><input type="text" id="installDate" name="installDate" /></td>
	            </tr>
	     		<tr><th><fmt:message key="aimir.phoneNumber"/></th>
	                <td><input type="text" id="phoneNumber" name="phoneNumber" /></td>
	            	<th id="modemIpSection_1"></th>
	            	<td id="modemIpSection_2"></td>
	            	<th id="modemIpSection_3"></th>
	            	<td id="modemIpSection_4"></td>
	            </tr>
	            </table>

				<input type="hidden" id="supplierId" name="supplier.id" />
				<input type="hidden" id="modemTypeName"    name="modemTypeName" value=""/>
				<input type="hidden" id="protocolTypeName" name="protocolTypeName" value=""/>
				
				<input type="hidden" id="hwVerName" name="hwVer" value=""/>
				<input type="hidden" id="fwVerName" name="fwVer" value=""/>
	   </form>
     </div>
<script>
   // 조회후 초기화
   initModemInfoDiv();
</script>