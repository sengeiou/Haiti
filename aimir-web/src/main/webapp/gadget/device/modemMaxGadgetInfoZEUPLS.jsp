<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8"
	contentType="text/html;charset=utf-8"%>
<%@ include file="/taglibs.jsp"%>


    <!-- 수정Div -->
    <div id="modemInfoEdit" style="display:block;">
	   <form id="modemInfoForm">

				<!--ZEUPLS_Edit-->
	            <table class="wfree" id="tableEdit" style="display:none">
	            <tr>
	            	<!--공용 필드-->
	            	<%@ include file="./modemMaxGadgetInfoEdit.jsp" %>
	               	
	               	<th><fmt:message key="aimir.channelid" /></th>
                    <td class="padding-r20px"><input type="text" id="channelId" name="channelId" />
                    <input type="hidden" id="channelIdHidden" value="<c:catch>${modem.channelId}</c:catch>"/></td>
                        
                    <th><fmt:message key="aimir.panid" /></th>
                    <td class="padding-r20px"><input type="text" id="panId" name="panId" />
                    <input type="hidden" id="panIdHidden" value="<c:catch>${modem.panId}</c:catch>"/></td>
                
                    
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

				<!--ZEUPLS_View-->
                <table class="wfree" id="tableView" style="display:none">
       				<tr>
                 
                 		<!--공용 필드-->
	            		<%@ include file="./modemMaxGadgetInfoView.jsp" %>

	                    <th><fmt:message key="aimir.channelid" /></th>
	                    <td class="padding-r20px"><input type="text" id="channelIdView" class="border-trans" readonly="readonly" /></td>
	                
	                	<th><fmt:message key="aimir.panid" /></th>
	                    <td class="padding-r20px"><input type="text" id="panIdView" class="border-trans" readonly="readonly" /></td>
	                    
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

   // 1ch
   infoDelayInterval = setInterval("infoDelay()",100);

   // 설치 지역 초기화
   locationTreeGoGo('treeDiv_1', 'searchWord_1', 'infolocationId');   

   function modemInfoDetailLoad(){
	   
	   $('#channelId').val($('#channelIdHidden').val());
       $('#channelIdView').val($('#channelIdHidden').val());

       $('#panId').val($('#panIdHidden').val());
       $('#panIdView').val($('#panIdHidden').val());

       $('#rfPower').val($('#rfPowerHidden').val());
       $('#rfPowerView').val($('#rfPowerHidden').val());

       $('#nodeKind').val($('#nodeKindHidden').val());
       $('#nodeKindView').val($('#nodeKindHidden').val());
    
       // 스케쥴 Clear / 셋팅
       initModemSchedule();
       
       $("#modemScheduleForm :input[id='meteringDay']").val("${modem.meteringDay}");
       $("#modemScheduleForm :input[id='meteringHour']").val("${modem.meteringHour}");
       
       $("#modemScheduleForm :input[id='lpPeriod']").option("${modem.lpPeriod}");
       $("#modemScheduleForm :input[id='alarmFlag']").option("");
       $("#modemScheduleForm :input[id='lpChoice']").option("${modem.lpChoice}");

       if (supplierName.search('MOE') >= 0){
       	// 공급사가 이라크MOE인 경우 setModemSchedule();건너뜀
        }else{
       		setModemSchedule();
        }       
       // modem의 위치정보 할당
       setModemLocInfo("${modem.gpioX}","${modem.gpioY}","${modem.gpioZ}","${modem.address}");   

   }

</script>