<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>


    <!-- 수정Div -->
    <div id="modemInfoEdit" style="display:block;">
	   <form id="modemInfoForm">
	       <div class="info-tabinside">
	            <br style="font-size:10px;">ZMU_Edit
	            <table>
	            <tr><td width="65px" class="graybold11pt"><fmt:message key="aimir.modemid"/></td>
	                <td class="min-width-130">
	                   <input type="text" id="modemId" name="deviceSerial" />
                       <input type="hidden" id="modemIdHidden" value="<c:catch>${modem.deviceSerial}</c:catch>"/></td>

	                <td width="30px">&nbsp;</td>
	                <td width="60px" class="graybold11pt"><fmt:message key="aimir.modem.type"/></td>
	                <td class="min-width-130">
	                   <select id="modemType"></select>
	                   <input type="hidden" id="modemTypeHidden" value="<c:catch>${modem.modemType}</c:catch>"/></td>

	                <td width="30px">&nbsp;</td>
	                <td width="70px" class="graybold11pt"><fmt:message key="aimir.mcuid"/></td>
	                <td class="min-width-130">
	                   <input type="text" id="mcuId" name="mcu.id" />
	                   <input type="hidden" id="mcuIdHidden" value="<c:catch>${modem.mcu.id}</c:catch>"/></td>
	            </tr>
	            <tr><td width="65px" class="graybold11pt"><fmt:message key="aimir.fw.hwversion"/></td>
	                <td class="min-width-130">
	                   <select id="hwVersion" name="hwVer"></select>
	                   <input type="hidden" id="hwVersionHidden" value="<c:catch>${modem.hwVer}</c:catch>"/></td>

	                <td width="30px">&nbsp;</td>
	                <td width="60px" class="graybold11pt"><fmt:message key="aimir.sw.version"/></td>
	                <td class="min-width-130">
	                   <select id="swVersion" name="swVer"></select>
	                   <input type="hidden" id="swVersionHidden" value="<c:catch>${modem.swVer}</c:catch>"/></td>

	                <td width="30px">&nbsp;</td>
	                <td width="70px" class="graybold11pt"><fmt:message key="aimir.protocol"/></td>
	                <td class="min-width-130">
	                   <select id="protocolType" ></select>
	                   <input type="hidden" id="protocolTypeHidden" value="<c:catch>${modem.protocolType}</c:catch>"/></td>
	            </tr>
	            <tr><td width="65px" class="graybold11pt"><fmt:message key="aimir.vendor"/></td>
	                <td class="min-width-130">
	                   <select id="deviceVendor" name="model.deviceVendor.id" onChange="javascript:getModelListByVendor(0);"></select>
	                   <input type="hidden" id="deviceVendorHidden" value="<c:catch>${modem.model.deviceVendor.id}</c:catch>"/></td>

	                <td width="30px">&nbsp;</td>
	                <td width="60px" class="graybold11pt"><fmt:message key="aimir.model"/></td>
	                <td class="min-width-130">
	                   <select id="modelId" name="model.id"></select>
	                   <input type="hidden" id="modelIdHidden" value="<c:catch>${modem.model.id}</c:catch>"/></td>

	                <td width="30px">&nbsp;</td>
	                <td width="70px" class="graybold11pt"><fmt:message key="aimir.installdate"/></td>
	                <td class="min-width-130">
	                   <input type="text" id="installDate" name="installDate" />
	                   <input type="hidden" id="installDateHidden" value="<c:catch>${modem.installDate}</c:catch>"/></td>
	            </tr>
	            </table>
	       </div>
	                <input type="hidden" id="supplierId"       name="supplier.id" />
	                <input type="hidden" id="modemTableId"     name="id" />
	                <input type="hidden" id="modemTypeName"    name="modemTypeName" value=""/>
	                <input type="hidden" id="protocolTypeName" name="protocolTypeName" value=""/>
	   </form>
     </div>

     <!-- 조회Div  -->
     <div id="modemInfoView" style="display:none;">
       <form id="modemInfoForm">
           <div class="info-tabinside">
                <br style="font-size:10px;">ZMU_View
                <table>
                 <tr><td width="65px" class="graybold11pt"><fmt:message key="aimir.modemid"/></td>
                    <td class="min-width-130"><input type="text" id="modemIdView" /></td>
                    <td width="30px">&nbsp;</td>
                    <td width="60px" class="graybold11pt"><fmt:message key="aimir.modem.type"/></td>
                    <td class="min-width-130"><input type="text" id="modemTypeView" /></td>
                    <td width="30px">&nbsp;</td>
                    <td width="70px" class="graybold11pt"><fmt:message key="aimir.mcuid"/></td>
                    <td class="min-width-130"><input type="text" id="mcuIdView" /></td>
                </tr>
                <tr><td width="65px" class="graybold11pt"><fmt:message key="aimir.fw.hwversion"/></td>
                    <td class="min-width-130"><input type="text" id="hwVersionView" /></td>
                    <td width="30px">&nbsp;</td>
                    <td width="60px" class="graybold11pt"><fmt:message key="aimir.sw.version"/></td>
                    <td class="min-width-130"><input type="text" id="swVersionView" /></td>
                    <td width="30px">&nbsp;</td>
                    <td width="70px" class="graybold11pt"><fmt:message key="aimir.protocol"/></td>
                    <td class="min-width-130"><input type="text" id="protocolTypeView" /></td>
                </tr>
                <tr><td width="65px" class="graybold11pt"><fmt:message key="aimir.vendor"/></td>
                    <td class="min-width-130"><input type="text" id="deviceVendorView" /></td>
                    <td width="30px">&nbsp;</td>
                    <td width="60px" class="graybold11pt"><fmt:message key="aimir.model"/></td>
                    <td class="min-width-130"><input type="text" id="modelIdView" /></td>
                    <td width="30px">&nbsp;</td>
                    <td width="70px" class="graybold11pt"><fmt:message key="aimir.installdate"/></td>
                    <td class="min-width-130"><input type="text" id="installDateView" /></td>
                </tr>
                </table>
           </div>
       </form>
     </div>
<script>

   // selectBox 초기화
   initModemInfoDiv();

   // 1ch
   infoDelayInterval = setInterval("infoDelay()",100);

   function modemInfoDetailLoad(){

	    alert('<fmt:message key="aimir.modemtypily"/>');

   }

</script>