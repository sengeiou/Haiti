<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>


    <!-- 수정Div -->
    <div id="modemInfoEdit" style="display:block;">
	   <form id="modemInfoForm">

				<!--PLCIU_Edit-->
	            <table class="wfree" id="tableEdit" style="display:none">
		            <tr>
		            	<!--공용 필드-->
		            	<%@ include file="./modemMaxGadgetInfoEdit.jsp" %>
	            	
	                    <th><fmt:message key="aimir.ipaddress"/></th>
	                    <td class="padding-r20px">
	                        <input type="text" id="ipAddr" name="ipAddr" />
	                        <input type="hidden" id="ipAddrHidden" value="<c:catch>${modem.ipAddr}</c:catch>"/></td>
	                    <th><fmt:message key="aimir.macaddress"/></th>
	                    <td>
	                        <input type="text" id="macAddr" name="macAddr" />
	                        <input type="hidden" id="macAddrHidden" value="<c:catch>${modem.macAddr}</c:catch>"/></td>
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

				<!--MMIU_View-->
               <table class="wfree" id="tableView" style="display:none">
       				<tr>
                 		<!--공용 필드-->
	            		<%@ include file="./modemMaxGadgetInfoView.jsp" %>
	            		
	                    <th><fmt:message key="aimir.ipaddress"/></th>
	                    <td class="padding-r20px"><input type="text" id="ipAddrView" class="border-trans" readonly="readonly" /></td>
	                    <th><fmt:message key="aimir.macaddress"/></th>
	                    <td><input type="text" id="macAddrView" class="border-trans" readonly="readonly" /></td>
                	</tr>
                </table>
			<input type="hidden" id="modelName" value="${modem.model.name}">
       </form>
     </div>
<script>
	reSortTable("tableEdit",3);
	reSortTable("tableView",3);
    // selectBox 초기화
    initModemInfoDiv();

    // 설치 지역 초기화
    locationTreeGoGo('treeDiv_1', 'searchWord_1', 'infolocationId');

    // 1ch
    infoDelayInterval = setInterval("infoDelay()",100);

    function modemInfoDetailLoad(){

        $('#ipAddr').val($('#ipAddrHidden').val());
        $('#ipAddrView').val($('#ipAddrHidden').val());

        $('#macAddr').val($('#macAddrHidden').val());
        $('#macAddrView').val($('#macAddrHidden').val());

	    //alert("<fmt:message key='aimir.modem.searchbytype' />");
	    initModemSchedule();

	    // modem의 위치정보 할당
        setModemLocInfo("${modem.gpioX}","${modem.gpioY}","${modem.gpioZ}","${modem.address}");	    

    }

</script>