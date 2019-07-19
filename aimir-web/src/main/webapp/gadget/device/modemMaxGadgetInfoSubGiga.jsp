<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

    <!-- 수정Div -->
    <div id="modemInfoEdit" style="display:block;">
	   <form id="modemInfoForm">
	            <table class="wfree" id="tableEdit">
		            <!-- 공용 필드 (S) -->
		            <tr>
		            	<th><fmt:message key="aimir.modemid" /></th>
						<td class="padding-r20px"><input type="text" id="modemId" name="deviceSerial" /> <input type="hidden" id="modemIdHidden" value="<c:catch>${modem.deviceSerial}</c:catch>" /></td>
						<th><fmt:message key="aimir.modem.type" /></th>
						<td class="padding-r20px"><select id="modemType"></select> <input type="hidden" id="modemTypeHidden" value="<c:catch>${modem.modemType}</c:catch>" /></td>
						<th><fmt:message key="aimir.mcuid" /></th>
						<td><input type="text" id="mcuId" name="mcu.sysID" /> <input type="hidden" id="mcuIdHidden" value="<c:catch>${modem.mcu.sysID}</c:catch>" /></td>
		            </tr>
		            <tr>
		            	<th><fmt:message key="aimir.fw.hwversion" /></th>
						<td class="padding-r20px"><input type="text" id="hwVersion" value="<c:catch>${modem.hwVer}</c:catch>" /><input type="hidden" id="hwVersionHidden" value="<c:catch>${modem.hwVer}</c:catch>" /></td>
						<th><fmt:message key="aimir.mcucode.fmversion" /></th>
						<td class="padding-r20px"><input type="text" id="fwVersion" name="fwVer" value="<c:catch>${modem.fwVer}</c:catch>" /><input type="hidden" id="fwVersionHidden" value="<c:catch>${modem.fwVer}</c:catch>" /></td>
						<th><fmt:message key="aimir.mcucode.fmbuild" /></th>
						<td class="padding-r20px"><input type="text" id="fwRevision" name="fwRevision" value="<c:catch>${modem.fwRevision}</c:catch>" /> <input type="hidden" id="fwRevisionHidden" value="<c:catch>${modem.fwRevision}</c:catch>" /></td>
		            </tr>
		            <tr>
						<th><fmt:message key="aimir.protocol" /></th>
						<td><select id="protocolType"></select> <input type="hidden" id="protocolTypeHidden" value="<c:catch>${modem.protocolType}</c:catch>" /></td>
						<th><fmt:message key="aimir.vendor" /></th>
						<td class="padding-r20px"><select id="deviceVendor" name="model.deviceVendor.id" onChange="javascript:getModelListByVendor(0);"></select> <input type="hidden" id="deviceVendorHidden" value="<c:catch>${modem.model.deviceVendor.id}</c:catch>" /></td>
						<th><fmt:message key="aimir.model" /></th>
						<td class="padding-r20px"><select id="modelId" name="model.id"></select> <input type="hidden" id="modelIdHidden" value="<c:catch>${modem.model.id}</c:catch>" /></td>
		            </tr>
		            <tr>
		            	<th><fmt:message key="aimir.location" /></th>
						<td><input name="searchWord_1" id='searchWord_1' type="text" style="width: 90px" value="${modem.location.name}" /> <input type='hidden' id='infolocationId' name="location.id" value=''></input></td>
						<th><fmt:message key="aimir.installationdate" /></th>
						<td><input type="text" id="installDate" name="installDate" /> <input type="hidden" id="installDateHidden" value="<c:catch>${modem.installDate}</c:catch>" /></td>
						<th><fmt:message key="aimir.txpower" /></th>
						<td><input type="text" id="rfPower" value="<c:catch>${modem.rfPower}</c:catch>" name="rfPower" /> <input type="hidden" id="rfPowerHidden"/></td>
		            </tr>
		            <tr>
		            	<th><fmt:message key="aimir.node.kind" /></th>
						<td class="padding-r20px"><input type="text" id="nodeKind" name="nodeKind" /> <input type="hidden" id="nodeKindHidden" value="<c:catch>${modem.nodeKind}</c:catch>" /></td>
						<th><fmt:message key="aimir.lastlinktime" /></th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="${lastLinkTime}" /></td>
						<th><fmt:message key="aimir.status" /></th>
						<td><select id="modemStatusEdit"></select> <input type="hidden" id="modemStatusCodeId" name="modemStatusCodeId" value="<c:catch>${modem.modemStatus.id}</c:catch>" /></td>
		            </tr>
		            <!-- 공용 필드 (E) -->
		            <tr>
						<th>IPv4 Address</th>
	                    <td class="padding-r20px"><input type="text" id="ipAddr" name="ipAddr" value="<c:catch>${modem.ipAddr}</c:catch>" /></td>
 	                    <th><fmt:message key="aimir.module.version"/></th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.moduleVersion}</c:catch>" /></td>
  						<th><fmt:message key="aimir.module.build"/></th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.moduleRevision}</c:catch>" /></td>
		            </tr>
		            <tr>
		            	<th><fmt:message key='aimir.ipv6address'/></th>
                        <td colspan="4"><input style="width: 464px !important;" type="text" id="ipv6Address" name="ipv6Address" value="<c:catch>${modem.ipv6Address}</c:catch>" /></td>
		            </tr>
		            <tr>
		            <%--
		            	<th><fmt:message key='aimir.securitykey'/></th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.securityKey}" /></td>
	                --%>    
	                    <th><fmt:message key='aimir.hopcount'/></th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${hops}" /></td>
	                <%--
	                    <th><fmt:message key='aimir.bandwidth'/></th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.bandWidth}" /></td>
		            </tr>
		            <tr>
		            	<th><fmt:message key='aimir.basestationaddress'/></th>
                        <td><input type="text" class="border-trans" readonly="readonly" value="${modem.baseStationAddress}" /></td>
                        <th><fmt:message key='aimir.comm.frequency'/></th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.frequency}" /></td>
	                --%>
	                    <th>GS1</th>
			            <td><input type="text" id="modemGs1" value="${modem.gs1}" class="border-trans gray11pt"  readonly/></td>
			            <th>Parent Node ID</th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${parentDeviceSerial}</c:catch>" /></td>
		            </tr>
		            <tr>
						<th>RSSI</th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.rssi}</c:catch>" /></td>
						<th>LQI</th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.lqi}</c:catch>" /></td>
						<th>ETX</th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.etx}</c:catch>" /></td>
		            </tr>
		            <tr>
		            	<th>CPU Usage(%)</th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.cpuUsage}</c:catch>" /></td>
						<th>Memory Usage(%)</th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.memoryUsage}</c:catch>" /></td>
		            </tr>
	            </table>
	            
				<div class="clear">
				 <div id="treeDiv_1Outer" class="tree-billing auto" style="display:none;">
				      <div id="treeDiv_1"></div>
				 </div>
				</div>
				<input type="hidden" id="supplierId"       name="supplier.id" />
				<input type="hidden" id="modemTableId"     name="id" />
				<input type="hidden" id="modemTypeName"    name="modemTypeName" value=""/>
				<input type="hidden" id="protocolTypeName" name="protocolTypeName" value=""/>
				
				<input type="hidden" id="hwVerName" name="hwVer" value=""/>
                <input type="hidden" id="swVerName" name="swVer" value=""/>

	   </form>
     </div>

     <!-- 조회Div  -->
     <div id="modemInfoView" style="display:none;">
       <form id="modemInfoForm">

                <table class="wfree" id="tableView">
	                <!-- 공용 필드 (S) -->
                	<tr>
	            		<th><fmt:message key="aimir.modemid" /></th>
						<td class="padding-r20px"><input type="text" id="modemIdView" class="border-trans" readonly="readonly" /></td>
						<th><fmt:message key="aimir.modem.type" /></th>
						<td class="padding-r20px"><input type="text" id="modemTypeView" class="border-trans" readonly="readonly" /></td>
						<th><fmt:message key="aimir.mcuid" /></th>
						<td><input type="text" id="mcuIdView" class="border-trans " readonly="readonly" value="<c:catch>${modem.mcu.sysID}</c:catch>" /></td>
					</tr>
					<tr>
						<th><fmt:message key="aimir.fw.hwversion" /></th>
						<td class="padding-r20px"><input type="text" id="hwVersionView" class="border-trans" readonly="readonly" /></td>
						<th><fmt:message key="aimir.mcucode.fmversion" /></th>
						<td class="padding-r20px"><input type="text" id="fwVersionView" class="border-trans " readonly="readonly" value="<c:catch>${modem.fwVer}</c:catch>" /></td>
						<th><fmt:message key="aimir.mcucode.fmbuild" /></th>
						<td class="padding-r20px"><input type="text" id="fwBuildView" class="border-trans" readonly="readonly" value="<c:catch>${modem.fwRevision}</c:catch>" /></td>
					</tr>
					<tr>
						<th><fmt:message key="aimir.protocol" /></th>
						<td><input type="text" id="protocolTypeView" class="border-trans " readonly="readonly" /></td>
						<th><fmt:message key="aimir.vendor" /></th>
						<td class="padding-r20px"><input type="text" id="deviceVendorView" class="border-trans" readonly="readonly" /></td>
						<th><fmt:message key="aimir.model" /></th>
						<td class="padding-r20px"><input type="text" id="modelIdView" class="border-trans" readonly="readonly" /></td>
					</tr>
					<tr>	
						<th><fmt:message key="aimir.location" /></th>
						<td><input type="text" id="locationView" class="border-trans " readonly="readonly" /></td>
						<th><fmt:message key="aimir.installationdate" /></th>
						<td><input type="text" class="border-trans" readonly="readonly" value="${installDate}" /></td>
						<th><fmt:message key="aimir.txpower" /></th>
						<td><input type="text" id="txPowerView" class="border-trans" readonly="readonly" value = "${txSize}"/></td>
					</tr>	
					<tr>
						<th><fmt:message key="aimir.node.kind" /></th>
						<td class="padding-r20px"><input type="text" id="nodeKindView" class="border-trans" readonly="readonly" /></td>
						<th><fmt:message key="aimir.lastlinktime" /></th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="${lastLinkTime}" /></td>
						<th><fmt:message key="aimir.status" /></th>
						<td class="padding-r20px"><input type="text" class="border-trans" id='modemStatusView' readonly="readonly" value="${modem.modemStatus.descr}" /></td>
	            	</tr>
	            	<!-- 공용 필드 (E) -->
	            	<tr>
						<th>IPv4 Address</th>
	                    <td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="${modem.ipAddr}" /></td>
 	                    <th><fmt:message key="aimir.module.version"/></th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.moduleVersion}</c:catch>" /></td>
  						<th><fmt:message key="aimir.module.build"/></th>
						<td class="padding-r20px"><input type="text" class="border-trans" readonly="readonly" value="<c:catch>${modem.moduleRevision}</c:catch>" /></td>
	                </tr>
	                <tr>
						<th><fmt:message key='aimir.ipv6address'/></th>
						<td colspan="4"><input style="width: 464px !important;" type="text" class="border-trans" readonly="readonly" value="${modem.ipv6Address}" /></td>
					</tr>
					<tr>
					 <%--
	                    <th><fmt:message key='aimir.securitykey'/></th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.securityKey}" /></td>
	                 --%>
	                    <th><fmt:message key='aimir.hopcount'/></th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.hopsToBaseStation}" /></td>
	                <%--
	                    <th><fmt:message key='aimir.bandwidth'/></th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.bandWidth}" /></td>
	                </tr>    
	                <tr>
		                <th><fmt:message key='aimir.basestationaddress'/></th>
						<td><input type="text" class="border-trans" readonly="readonly" value="${modem.baseStationAddress}" /></td>
	                    <th><fmt:message key='aimir.comm.frequency'/></th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.frequency}" /></td>
	                 --%>
	                    <th>GS1</th>
			            <td><input type="text" id="modemGs1" value="${modem.gs1}" class="border-trans gray11pt"  readonly/></td>
	                    <th>Parent Node ID</th>
			            <td><input type="text" id="pan_idView" class="border-trans gray11pt"  readonly="readonly" value="<c:catch>${parentDeviceSerial}</c:catch>"/></td>
                	</tr>
                	<tr>
	           			<th>RSSI</th>
						<td class="padding-r20px"><input type="text" id="rssiView" class="border-trans" readonly="readonly" value="<c:catch>${modem.rssi}</c:catch>" /></td>
	           			<th>LQI</th>
						<td class="padding-r20px"><input type="text" id="lqiView" class="border-trans" readonly="readonly" value="<c:catch>${modem.lqi}</c:catch>" /></td>
						<th>ETX</th>
						<td class="padding-r20px"><input type="text" id="etxView" class="border-trans " readonly="readonly" value="<c:catch>${modem.etx}</c:catch>" /></td>
                	</tr>
                	<tr>
                		<th>CPU Usage(%)</th>
						<td class="padding-r20px"><input type="text" id="cpuUsageView" class="border-trans" readonly="readonly" value="<c:catch>${modem.cpuUsage}</c:catch>" /></td>
	           			<th>Memory Usage(%)</th>
						<td class="padding-r20px"><input type="text" id="memoryUsageView" class="border-trans" readonly="readonly" value="<c:catch>${modem.memoryUsage}</c:catch>" /></td>
                	</tr>
                </table>
			<input type="hidden" id="modelName" value="${modem.model.name}">
       </form>
     </div>
<script>
	// reSortTable("tableEdit",3);
	// reSortTable("tableView",3);
	
    // selectBox 초기화
    initModemInfoDiv();

    // 설치 지역 초기화
    locationTreeGoGo('treeDiv_1', 'searchWord_1', 'infolocationId');

    // 1ch
    infoDelayInterval = setInterval("infoDelay()",100);

    function modemInfoDetailLoad(){

	    //alert("<fmt:message key='aimir.modem.searchbytype' />");
	    initModemSchedule();

	    // modem의 위치정보 할당
        setModemLocInfo("${modem.gpioX}","${modem.gpioY}","${modem.gpioZ}","${modem.address}");	    

   }

</script>