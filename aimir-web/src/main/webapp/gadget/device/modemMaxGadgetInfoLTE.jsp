<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>

    <!-- 수정Div -->
    <div id="modemInfoEdit" style="display:block;">
	   <form id="modemInfoForm">

			
	            <table class="wfree" id="tableEdit" style="display:none">
		            <tr>
		            	<!--공용 필드-->
		            	<%@ include file="./modemMaxGadgetInfoEdit.jsp" %>	            			
	            		<th>RSRP</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.rsrp}" /></td>
	                    <th>RSRQ</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.rsrq}" /></td>	                    
	                    <th>PLMN</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.plmn}" /></td>
	                    <th>제작사</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.prodMaker}" /></td>
	                    <th>제조년월일</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.prodMakeDate}" /></td>
	                    <th>제조번호</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.prodSerial}" /></td>
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

                <table class="wfree" id="tableView" style="display:none">
       				<tr>
                 		<!--공용 필드-->
	            		<%@ include file="./modemMaxGadgetInfoView.jsp" %>
	            		<th>RSRP</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.rsrp}" /></td>
	                    <th>RSRQ</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.rsrq}" /></td>	                    
	                    <th>PLMN</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.plmn}" /></td>
	                    <th>제작사</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.prodMaker}" /></td>
	                    <th>제조년월일</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.prodMakeDate}" /></td>
	                    <th>제조번호</th>
	                    <td><input type="text" class="border-trans" readonly="readonly" value="${modem.prodSerial}" /></td>
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

	    //alert("<fmt:message key='aimir.modem.searchbytype' />");
	    initModemSchedule();

	    // modem의 위치정보 할당
        setModemLocInfo("${modem.gpioX}","${modem.gpioY}","${modem.gpioZ}","${modem.address}");	    

   }

</script>